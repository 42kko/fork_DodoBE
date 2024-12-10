package dododocs.dododocs.member.presentation;

import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import dododocs.dododocs.chatbot.dto.FindMemberInfoResponse;
import dododocs.dododocs.member.application.MemberService;
import dododocs.dododocs.member.dto.FindRegisterMemberRepoResponses;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/repos/all")
    public ResponseEntity<FindRepoNameListResponse> findMemberRepoAllList(@Authentication final Accessor accessor) {
        FindRepoNameListResponse findRepoNameListResponse = memberService.getUserRepositories(accessor.getId());
        return ResponseEntity.ok(findRepoNameListResponse);
    }

    @GetMapping("/repos/registered")
    public ResponseEntity<FindRegisterMemberRepoResponses> findMemberRepoRegisteredList(@Authentication final Accessor accessor) {
        return ResponseEntity.ok(memberService.findRegisterMemberRepoResponses(accessor.getId()));
    }

    @GetMapping("/info")
    public ResponseEntity<FindMemberInfoResponse> findMemberInfo(@Authentication final Accessor accessor) {
        return ResponseEntity.ok(memberService.findMemberInfo(accessor.getId()));
    }
}
