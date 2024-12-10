package dododocs.dododocs.chatbot.dto;

import lombok.Getter;

@Getter
public class FindMemberInfoResponse {
    private String nickname;

    private FindMemberInfoResponse() {
    }

    public FindMemberInfoResponse(final String nickname) {
        this.nickname = nickname;
    }
}
