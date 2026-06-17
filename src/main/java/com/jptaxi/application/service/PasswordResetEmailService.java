package com.jptaxi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class PasswordResetEmailService {

    private static final String SUBJECT = "Restaurant Finder パスワードリセット";

    private final ResendEmailClient resendEmailClient;
    private final long expirationMinutes;

    public PasswordResetEmailService(
            ResendEmailClient resendEmailClient,
            @Value("${app.password-reset.expiration-minutes:30}") long expirationMinutes
    ) {
        this.resendEmailClient = resendEmailClient;
        this.expirationMinutes = expirationMinutes;
    }

    public void sendResetLink(String email, String resetLink) {
        String safeResetLink = HtmlUtils.htmlEscape(resetLink);
        String html = """
                <!doctype html>
                <html lang="ja">
                <body style="margin:0;background:#f4f7fb;font-family:Arial,sans-serif;color:#172033">
                  <div style="max-width:560px;margin:32px auto;padding:32px;background:#ffffff;border-radius:16px">
                    <h1 style="font-size:24px;margin:0 0 16px">パスワードをリセット</h1>
                    <p>Restaurant Finderアカウントのパスワードリセットがリクエストされました。</p>
                    <p style="margin:28px 0">
                      <a href="%s" style="display:inline-block;padding:12px 20px;background:#0066cc;color:#ffffff;text-decoration:none;border-radius:10px">
                        パスワードをリセットする
                      </a>
                    </p>
                    <p>このリンクは %d 分後に無効になり、1回のみ使用できます。</p>
                    <p>ボタンが機能しない場合は、次のリンクを開いてください：</p>
                    <p style="word-break:break-all"><a href="%s">%s</a></p>
                    <p>パスワードリセットをリクエストしていない場合は、このメールを無視してください。</p>
                  </div>
                </body>
                </html>
                """.formatted(safeResetLink, expirationMinutes, safeResetLink, safeResetLink);
        String text = """
                こんにちは、

                Restaurant Finderアカウントのパスワードリセットがリクエストされました。
                以下のリンクを開いて新しいパスワードを設定してください：

                %s

                このリンクは %d 分後に無効になり、1回のみ使用できます。
                パスワードリセットをリクエストしていない場合は、このメールを無視してください。
                """.formatted(resetLink, expirationMinutes);

        resendEmailClient.send(email, SUBJECT, html, text);
    }
}
