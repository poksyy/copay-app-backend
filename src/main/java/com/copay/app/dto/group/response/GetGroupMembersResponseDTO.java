package com.copay.app.dto.group.response;

import com.copay.app.dto.group.auxiliary.ExternalMemberDTO;
import com.copay.app.dto.group.auxiliary.RegisteredMemberDTO;

import java.util.List;

public class GetGroupMembersResponseDTO {

    List<ExternalMemberDTO> externalMembers;

    List<RegisteredMemberDTO> registeredMembers;

    public GetGroupMembersResponseDTO(List<RegisteredMemberDTO> registeredMembers, List<ExternalMemberDTO> externalMembers) {
        this.registeredMembers = registeredMembers;
        this.externalMembers = externalMembers;
    }

    public List<RegisteredMemberDTO> getRegisteredMembers() {
        return registeredMembers;
    }

    public void setRegisteredMembers(List<RegisteredMemberDTO> registeredMembers) {
        this.registeredMembers = registeredMembers;
    }

    public List<ExternalMemberDTO> getExternalMembers() {
        return externalMembers;
    }

    public void setExternalMembers(List<ExternalMemberDTO> externalMembers) {
        this.externalMembers = externalMembers;
    }
}
