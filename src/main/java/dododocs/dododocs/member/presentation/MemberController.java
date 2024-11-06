package dododocs.dododocs.member.presentation;

import dododocs.dododocs.member.application.MemberService;
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
    public ResponseEntity<Void> findMemberRepoList() {
        List<String> names = memberService.getUserRepositories("msung99");
        for(String name : names) {
            System.out.println(name);
        }
        return ResponseEntity.ok().build();
    }
}
