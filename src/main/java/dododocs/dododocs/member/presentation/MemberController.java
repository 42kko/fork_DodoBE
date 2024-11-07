package dododocs.dododocs.member.presentation;

import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import dododocs.dododocs.member.application.MemberService;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/repos")
    public ResponseEntity<FindRepoNameListResponse> findMemberRepoList(@Authentication final Accessor accessor) {
        FindRepoNameListResponse findRepoNameListResponse = memberService.getUserRepositories(accessor.getId());
        return ResponseEntity.ok(findRepoNameListResponse);
    }
}
