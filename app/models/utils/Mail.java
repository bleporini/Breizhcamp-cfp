package models.utils;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;
import play.Configuration;
import play.Logger;
import play.libs.Akka;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Send a mail with Play20StartApp.
 * <p/>
 * User: yesnault
 * Date: 24/01/12
 */
public class Mail {

    /** 1 second delay on sending emails */
    private static final int DELAY = 1;

    /**
     * Envelop to prepare.
     */
    public static class Envelop {
        private String subject;
        private String message;
        private Collection<String> toEmails;

        /**
         * Constructor of Envelop.
         *
         * @param subject  the subject
         * @param message  a message
         * @param toEmails list of emails adress
         */
        public Envelop(String subject, String message, Collection<String> toEmails) {
            this.subject = subject;
            this.message = message;
            this.toEmails = toEmails;
        }

        public Envelop(String subject, String message, String email) {
            this.message = message;
            this.subject = subject;
            this.toEmails = new ArrayList<String>();
            this.toEmails.add(email);
        }
    }

    /**
     * Send a email, using Akka to offload it to an actor.
     *
     * @param envelop envelop to send
     */
    public static void sendMail(Mail.Envelop envelop) {
        EnvelopJob envelopJob = new EnvelopJob(envelop);
        final FiniteDuration delay = Duration.create(DELAY, TimeUnit.SECONDS);
        Akka.system().scheduler().scheduleOnce(delay, envelopJob, null);
    }

    static class EnvelopJob implements Runnable {
        Mail.Envelop envelop;

        public EnvelopJob(Mail.Envelop envelop) {
            this.envelop = envelop;
        }

        public void run() {

            final Configuration root = Configuration.root();
            final String mailFrom = root.getString("mail.from");

            final String mailSign = root.getString("mail.sign");
			String messageText = envelop.message + "\n\n " + mailSign;
			String messageHtml = envelop.message + "<br><br>--<br>" + mailSign;

			for (String toEmail : envelop.toEmails) {
				sendEmail(mailFrom, messageText, messageHtml, envelop.subject,
						toEmail);
			}

            Logger.debug("Mail sent - SMTP: {} : {} SSL: {} user: {} password: {} message: {}",
                    root.getString("smtp.host"),
                    root.getString("smtp.port"),
                    root.getString("smtp.ssl"),
                    root.getString("smtp.user"),
                    root.getString("smtp.password"),
                    messageText);
        }

		private void sendEmail(final String mailFrom, String messageText,
				String messageHtml, String subject, String toEmail) {
			MailerAPI email = play.Play.application().plugin(MailerPlugin.class).email();
			email.setFrom(mailFrom);
			email.setSubject(subject);
			email.setRecipient(toEmail);
			Logger.debug("Mail.sendMail: Mail will be sent to {}", toEmail);
			email.send(messageText, messageHtml);
		}
    }
}
