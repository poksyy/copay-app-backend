package com.copay.app.repository;

import com.copay.app.entity.relations.GroupMember;
import com.copay.app.entity.relations.GroupMemberId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

	// Retrieves all GroupMember records for the given user id.
	List<GroupMember> findByIdUserUserId(Long userId);
}