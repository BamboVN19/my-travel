package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.TripMember;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.BadRequestException;
import com.mytravel.api.exception.DuplicateResourceException;
import com.mytravel.api.exception.ForbiddenException;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.TripMemberRepository;
import com.mytravel.api.repository.TripRepository;
import com.mytravel.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMemberRepository tripMemberRepository;
  private final UserRepository userRepository;
  private final UserService userService;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Transactional
  public void updateTripStatuses() {
    LocalDate today = LocalDate.now();
    int ongoingUpdated = tripRepository.updateTripStatusToOngoing(today);
    int completedUpdated = tripRepository.updateTripStatusToCompleted(today);
    log.info("Trip status auto-update completed: {} set to ONGOING, {} set to COMPLETED", ongoingUpdated, completedUpdated);
  }

  private String determineStatus(LocalDate startDate, LocalDate endDate, String requestedStatus) {
    if (requestedStatus != null && !requestedStatus.trim().isEmpty() && !"PLANNED".equalsIgnoreCase(requestedStatus.trim())) {
      return requestedStatus.trim().toUpperCase();
    }
    LocalDate today = LocalDate.now();
    if (today.isBefore(startDate)) {
      return "PLANNED";
    } else if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
      return "ONGOING";
    } else {
      return "COMPLETED";
    }
  }

  @Transactional
  public TripResponse createTrip(TripRequest request) {
    User currentUser = userService.getCurrentUser();

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BadRequestException("Ngày bắt đầu không thể sau ngày kết thúc!");
    }

    if (tripRepository.existsOverlappingTrip(currentUser.getId(), request.getStartDate(), request.getEndDate(), null)) {
      throw new DuplicateResourceException("Bạn đã có chuyến đi khác trùng trong khoảng thời gian từ " +
          request.getStartDate().format(DATE_FORMATTER) + " đến " +
          request.getEndDate().format(DATE_FORMATTER) + "!");
    }

    String initialStatus = determineStatus(request.getStartDate(), request.getEndDate(), request.getStatus());

    Trip trip = Trip.builder()
        .owner(currentUser)
        .title(request.getTitle().trim())
        .destination(request.getDestination().trim())
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .totalBudget(request.getTotalBudget())
        .status(initialStatus)
        .build();

    Trip savedTrip = tripRepository.save(trip);

    TripMember ownerMember = TripMember.builder()
        .trip(savedTrip)
        .user(currentUser)
        .role("OWNER")
        .build();

    tripMemberRepository.save(ownerMember);

    return mapToResponse(savedTrip, "OWNER", 1);
  }

  @Transactional(readOnly = true)
  public PageResponse<TripResponse> getTrips(String status, String search, Pageable pageable) {
    User currentUser = userService.getCurrentUser();
    String cleanStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
    String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

    Page<Trip> tripPage = tripRepository.searchUserTrips(currentUser.getId(), cleanStatus, cleanSearch, pageable);

    Page<TripResponse> responsePage = tripPage.map(trip -> {
      String role = getUserRoleInTrip(trip, currentUser.getId());
      long memberCount = tripMemberRepository.countByTripId(trip.getId());
      return mapToResponse(trip, role, memberCount);
    });

    return PageResponse.from(responsePage);
  }

  @Transactional(readOnly = true)
  public TripResponse getTripById(UUID id) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(id);
    checkUserTripAccess(trip, currentUser.getId());

    String role = getUserRoleInTrip(trip, currentUser.getId());
    long memberCount = tripMemberRepository.countByTripId(trip.getId());
    return mapToResponse(trip, role, memberCount);
  }

  @Transactional(readOnly = true)
  public TripResponse getCurrentTrip() {
    User currentUser = userService.getCurrentUser();
    LocalDate today = LocalDate.now();
    List<Trip> currentTrips = tripRepository.findCurrentTripsByUserId(currentUser.getId(), today);

    if (currentTrips.isEmpty()) {
      return null;
    }

    Trip currentTrip = currentTrips.get(0);
    String role = getUserRoleInTrip(currentTrip, currentUser.getId());
    long memberCount = tripMemberRepository.countByTripId(currentTrip.getId());
    return mapToResponse(currentTrip, role, memberCount);
  }

  @Transactional
  public TripResponse updateTrip(UUID id, TripRequest request) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(id);
    checkUserCanEdit(trip, currentUser.getId());

    if (request.getStartDate().isAfter(request.getEndDate())) {
      throw new BadRequestException("Ngày bắt đầu không thể sau ngày kết thúc!");
    }

    String newStatus = request.getStatus() != null ? request.getStatus() : trip.getStatus();
    if (!"CANCELLED".equalsIgnoreCase(newStatus)) {
      if (tripRepository.existsOverlappingTrip(currentUser.getId(), request.getStartDate(), request.getEndDate(), id)) {
        throw new DuplicateResourceException("Bạn đã có chuyến đi khác trùng trong khoảng thời gian từ " +
            request.getStartDate().format(DATE_FORMATTER) + " đến " +
            request.getEndDate().format(DATE_FORMATTER) + "!");
      }
    }

    trip.setTitle(request.getTitle().trim());
    trip.setDestination(request.getDestination().trim());
    trip.setStartDate(request.getStartDate());
    trip.setEndDate(request.getEndDate());
    if (request.getTotalBudget() != null) {
      trip.setTotalBudget(request.getTotalBudget());
    }

    if (request.getStatus() != null) {
      trip.setStatus(request.getStatus());
    } else {
      trip.setStatus(determineStatus(request.getStartDate(), request.getEndDate(), trip.getStatus()));
    }

    Trip updatedTrip = tripRepository.save(trip);
    String role = getUserRoleInTrip(updatedTrip, currentUser.getId());
    long memberCount = tripMemberRepository.countByTripId(updatedTrip.getId());
    return mapToResponse(updatedTrip, role, memberCount);
  }

  @Transactional
  public void deleteTrip(UUID id) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(id);
    if (trip.getOwner() == null || !trip.getOwner().getId().equals(currentUser.getId())) {
      throw new ForbiddenException("Chỉ người tạo chuyến đi mới có quyền xóa chuyến đi này!");
    }
    tripRepository.delete(trip);
  }

  @Transactional
  public TripMemberResponse addMember(UUID tripId, AddTripMemberRequest request) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(tripId);
    checkUserCanEdit(trip, currentUser.getId());

    User memberUser = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + request.getEmail()));

    if (tripMemberRepository.existsByTripIdAndUserId(tripId, memberUser.getId())) {
      throw new DuplicateResourceException("Người dùng này đã là thành viên của chuyến đi!");
    }

    if (tripRepository.existsOverlappingTrip(memberUser.getId(), trip.getStartDate(), trip.getEndDate(), trip.getId())) {
      throw new DuplicateResourceException("Người dùng " + (memberUser.getFullName() != null ? memberUser.getFullName() : memberUser.getUsername()) +
          " đã có chuyến đi khác trùng trong khoảng thời gian từ " +
          trip.getStartDate().format(DATE_FORMATTER) + " đến " +
          trip.getEndDate().format(DATE_FORMATTER) + "!");
    }

    TripMember tripMember = TripMember.builder()
        .trip(trip)
        .user(memberUser)
        .role(request.getRole() != null ? request.getRole() : "EDITOR")
        .build();

    TripMember saved = tripMemberRepository.save(tripMember);
    return mapToMemberResponse(saved);
  }

  @Transactional
  public void removeMember(UUID tripId, UUID userId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(tripId);
    checkUserCanEdit(trip, currentUser.getId());

    if (trip.getOwner() != null && trip.getOwner().getId().equals(userId)) {
      throw new ForbiddenException("Không thể xóa chủ chuyến đi (OWNER) khỏi danh sách thành viên!");
    }

    tripMemberRepository.deleteByTripIdAndUserId(tripId, userId);
  }

  @Transactional(readOnly = true)
  public List<TripMemberResponse> getMembers(UUID tripId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = findTripById(tripId);
    checkUserTripAccess(trip, currentUser.getId());

    return tripMemberRepository.findByTripId(tripId).stream()
        .map(this::mapToMemberResponse)
        .collect(Collectors.toList());
  }

  public Trip findTripById(UUID id) {
    return tripRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến đi với ID: " + id));
  }

  public void checkUserTripAccess(Trip trip, UUID userId) {
    boolean isOwner = trip.getOwner() != null && trip.getOwner().getId().equals(userId);
    boolean isMember = tripMemberRepository.existsByTripIdAndUserId(trip.getId(), userId);
    if (!isOwner && !isMember) {
      throw new ForbiddenException("Bạn không có quyền truy cập chuyến đi này!");
    }
  }

  public void checkUserCanEdit(Trip trip, UUID userId) {
    if (trip.getOwner() != null && trip.getOwner().getId().equals(userId)) {
      return;
    }
    TripMember member = tripMemberRepository.findByTripIdAndUserId(trip.getId(), userId)
        .orElseThrow(() -> new ForbiddenException("Bạn không phải là thành viên của chuyến đi này!"));

    if ("VIEWER".equalsIgnoreCase(member.getRole())) {
      throw new ForbiddenException("Thành viên với quyền VIEWER không có quyền chỉnh sửa chuyến đi!");
    }
  }

  private String getUserRoleInTrip(Trip trip, UUID userId) {
    if (trip.getOwner() != null && trip.getOwner().getId().equals(userId)) {
      return "OWNER";
    }
    return tripMemberRepository.findByTripIdAndUserId(trip.getId(), userId)
        .map(TripMember::getRole)
        .orElse("VIEWER");
  }

  private TripResponse mapToResponse(Trip trip, String role, long memberCount) {
    return TripResponse.builder()
        .id(trip.getId())
        .ownerId(trip.getOwner() != null ? trip.getOwner().getId() : null)
        .title(trip.getTitle())
        .destination(trip.getDestination())
        .startDate(trip.getStartDate())
        .endDate(trip.getEndDate())
        .totalBudget(trip.getTotalBudget())
        .status(trip.getStatus())
        .role(role)
        .memberCount(memberCount)
        .createdAt(trip.getCreatedAt())
        .build();
  }

  private TripMemberResponse mapToMemberResponse(TripMember member) {
    User user = member.getUser();
    return TripMemberResponse.builder()
        .id(member.getId())
        .userId(user != null ? user.getId() : null)
        .username(user != null ? user.getUsername() : null)
        .fullName(user != null ? user.getFullName() : null)
        .email(user != null ? user.getEmail() : null)
        .avatarUrl(user != null ? user.getAvatarUrl() : null)
        .role(member.getRole())
        .joinedAt(member.getJoinedAt())
        .build();
  }
}
