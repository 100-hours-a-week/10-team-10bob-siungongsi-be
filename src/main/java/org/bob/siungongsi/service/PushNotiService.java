package org.bob.siungongsi.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.GongsiEntity;
import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.repository.CompanyRepository;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("batch")
@Service
public class PushNotiService {

  private final FcmService fcmService;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  @Value("${site.url}")
  private String siteUrl;

  public PushNotiService(
      FcmService fcmService,
      NotificationRepository notificationRepository,
      UserRepository userRepository,
      CompanyRepository companyRepository) {
    this.fcmService = fcmService;
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
  }

  public boolean sendPushNotification(GongsiEntity gongsi) {
    List<NotiHistoryEntity> notiHistoryEntities =
        notificationRepository.findByCompanyId(gongsi.getCompany().getId());

    System.out.println(
        "Time : "
            + LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            + " - Sending push notification for "
            + notiHistoryEntities.size()
            + " subscribers");
    List<UserEntity> subscribers =
        userRepository.findAllById(
            notiHistoryEntities.stream().map(NotiHistoryEntity::getUserId).toList());
    for (UserEntity user : subscribers) {
      System.out.println(
          "Sending push notification to "
              + user.getId()
              + " "
              + user.getNotiFlag()
              + " - "
              + user.getPushTokenId());
      if (user.getPushTokenId() == null || user.getPushTokenId().isBlank()) {
        continue;
      }
      if (!sendMessage(user.getPushTokenId(), gongsi)) {
        return false;
      }
    }
    return true;
  }

  private boolean sendMessage(String token, GongsiEntity gongsi) {
    CompanyEntity company =
        companyRepository
            .findById(gongsi.getCompany().getId())
            .orElseThrow(() -> new RuntimeException("Company not found"));

    System.out.println(
        "Time : "
            + LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            + " - Sending push notification to "
            + token
            + " company: "
            + company.getCompanyName()
            + " gongsi: "
            + gongsi.getGongsiTitle());
    String title = company.getCompanyName() + " 기업의 새로운 공시가 나왔습니다.";
    String body = "공시 제목 : " + gongsi.getGongsiTitle();
    String url = siteUrl + "detail/" + gongsi.getId();
    fcmService.sendNotification(token, title, body, url);
    return true;
  }
}
