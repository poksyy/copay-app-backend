package com.copay.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.copay.app.entity.relations.ExternalMember;

@Repository
public interface ExternalMemberRepository extends JpaRepository<ExternalMember, Long> {
    
}
