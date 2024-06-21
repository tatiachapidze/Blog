package ge.bog.blog.service;

import ge.bog.blog.entity.AdEntity;
import ge.bog.blog.exceptions.AlreadyExistsException;
import ge.bog.blog.model.AddAdReq;
import ge.bog.blog.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    @Transactional
    public long addAd(AddAdReq ad) {
        Optional<AdEntity> existingAd = adRepository.findByTitle(ad.getTitle());
        if (existingAd.isPresent()) {
            throw new AlreadyExistsException("Ad with this title already exists");
        }
        AdEntity adEntity = new AdEntity();
        adEntity.setTitle(ad.getTitle());
        adEntity.setContent(ad.getContent());
        adEntity.setRpm(ad.getRpm());
        adEntity.setCompany(ad.getCompany());
        adRepository.save(adEntity);
        return adEntity.getId();
    }
}