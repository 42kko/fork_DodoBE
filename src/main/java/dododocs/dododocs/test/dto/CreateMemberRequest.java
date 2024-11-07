package dododocs.dododocs.test.dto;

import lombok.Getter;

@Getter
public class CreateMemberRequest {
    private String email;

    private CreateMemberRequest() {
    }

    public CreateMemberRequest(final String email) {
        this.email = email;
    }
}
