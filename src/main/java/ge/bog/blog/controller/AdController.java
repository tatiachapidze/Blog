package ge.bog.blog.controller;

import ge.bog.blog.model.AddAdReq;
import ge.bog.blog.service.AdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {
    private final AdService adService;

    @PostMapping(value = "/add-ad", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAd(@RequestBody @Valid AddAdReq ad){
        long id = adService.addAd(ad);
        return ResponseEntity.ok(id);
    }
}