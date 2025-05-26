package com.copay.app.repository;

import com.copay.app.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    // Retrieves all groups where the specified user is a registered member (via the GroupMember relationship).
    @Query("SELECT gm.id.group FROM GroupMember gm WHERE gm.id.user.userId = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);

}
