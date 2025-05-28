package com.copay.app.repository;

import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

	// Retrieves all GroupMember records for the given user id.
	// The method follows Spring Data JPA conventions to access the `userId`
	// within the embedded `GroupMemberId` object.
	List<GroupMember> findByIdUserUserId(Long userId);

	// Checks if a specific user belongs to a given group by their IDs.
	boolean existsByIdUserUserIdAndIdGroupGroupId(Long userId, Long groupId);
}