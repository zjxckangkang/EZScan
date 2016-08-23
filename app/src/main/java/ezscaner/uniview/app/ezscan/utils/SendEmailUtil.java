package ezscaner.uniview.app.ezscan.utils;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ezscaner.uniview.app.ezscan.R;
import ezscaner.uniview.app.ezscan.log.KLog;

/**
 * 邮件发送工具类
 *
 * @author Gunpoder
 */
public class SendEmailUtil {
    private static final boolean debug = true;

    private static final String PWD_KEY = "whatthis";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy年MM月dd日 HH:mm:ss");
    private static final String PWD_ENCRIPT = "t17/E5q7WZYuB4ITBOXlZWg39qLlpW5k";
    public static String versionName = "N/A";
    public Properties properties;
    public Session session;
    public Message message;
    public MimeMultipart multipart;
    private MailListener mMailListener;

    public void setMailListerner(MailListener mailListener) {
        mMailListener = mailListener;

    }

    public MailListener getMailListerner() {
        return mMailListener;
    }

    public void setProperties(String host, String post) {
        // 地址
        this.properties.put("mail.smtp.host", host);
        // 端口号
        this.properties.put("mail.smtp.post", post);
        // 是否验证
        this.properties.put("mail.smtp.auth", true);
        this.session = Session.getInstance(properties);
        this.message = new MimeMessage(session);
        this.multipart = new MimeMultipart("mixed");

    }

    public SendEmailUtil() {
        super();
        this.properties = new Properties();
    }

    /**
     * 设置收件人
     *
     * @param receiver
     * @throws MessagingException
     */
    private void setReceiver(String[] receiver) throws MessagingException {
        Address[] address = new InternetAddress[receiver.length];
        for (int i = 0; i < receiver.length; i++) {
            address[i] = new InternetAddress(receiver[i]);
        }
        this.message.setRecipients(Message.RecipientType.TO, address);
    }

    /**
     * 设置邮件
     *
     * @param from    来源
     * @param title   标题
     * @param content 内容
     * @throws AddressException
     * @throws MessagingException
     */
    private void setMessage(String from, String title, String content)
            throws AddressException,

            MessagingException {
        this.message.setFrom(new InternetAddress(from));
        this.message.setSubject(title);
        // 纯文本的话用setText()就行，不过有附件就显示不出来内容了
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setContent(content, "text/html; charset=utf-8");
        this.multipart.addBodyPart(textBody);
    }

    /**
     * 添加附件
     *
     * @param filePath 文件路径
     * @throws MessagingException
     */
    private void addAttachment(String filePath) throws MessagingException {
        FileDataSource fileDataSource = new FileDataSource(new File(filePath));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileDataSource.getName());
        this.multipart.addBodyPart(mimeBodyPart);
    }

    boolean sendLogSucceed = false;
    boolean hasCompletedSend = false;
    public boolean hasTimeOut = false;

    public void sendMail(Context context, String zipPath, String[] mailAddrs
            , MailListener mailListener) {
        KLog.i(debug);
        this.setMailListerner(mailListener);
        // 设置服务器地址和端口
        this.setProperties("smtp.163.com", "25");

        try {
            this.addAttachment(zipPath);
            this.setMessage("unidbg@163.com", buildTitle(context),
                    buildContent(context));
            this.setReceiver(mailAddrs);
            this.sendEmail("smtp.163.com", "unidbg@163.com",
                    DESUtil.decryptDES(PWD_ENCRIPT, SendEmailUtil.PWD_KEY));

            //超时认为失败
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!hasCompletedSend) {
                        if (mMailListener != null) {
                            hasTimeOut = true;
                            mMailListener.timeOut();
                            mMailListener.onFinish();

                            KLog.i("failed for time out");
                        }
                    }

                }
            }).start();

        } catch (Exception e) {
            KLog.i("Exception");

        }

    }

    /**
     * 发送邮件
     *
     * @param host    地址
     * @param account 账户名
     * @param pwd     密码
     * @throws MessagingException
     */
    private void sendEmail(final String host, final String account,
                           final String pwd) {
        new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    KLog.i("send thread run");
                    if (mMailListener != null) {
                        mMailListener.startSend();
                    }
                    // 发送时间
                    message.setSentDate(new Date());
                    // 发送的内容，文本和附件
                    message.setContent(multipart);
                    message.saveChanges();
                    // 创建邮件发送对象，并指定其使用SMTP协议发送邮件
                    Transport transport = session.getTransport("smtp");
                    // 登录邮箱
                    transport.connect(host, account, pwd);
                    // 发送邮件
                    KLog.i(debug);
                    transport.sendMessage(message, message.getAllRecipients());
                    // 关闭连接
                    transport.close();
                    sendLogSucceed = true;

                } catch (NoSuchProviderException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                hasCompletedSend = true;

                //超时情况下不提示
                if (!hasTimeOut) {

                    if (mMailListener != null) {
                        if (sendLogSucceed) {
                            KLog.i("send log sendLogSucceed");
                            mMailListener.succeedSend();
                            mMailListener.onFinish();
                        } else {
                            KLog.i("send log failed");
                            mMailListener.failSend();
                            mMailListener.onFinish();
                        }
                    } else {
                        KLog.i("mMailListener null");

                    }
                }


            }
        }.start();

    }

    private static String buildContent(Context context) {
        StringBuilder content = new StringBuilder();
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2>(该邮件来自"
                + context.getString(R.string.app_name)
                + "手机客户端)</FONT></STRONG></DIV>");
        return content.toString();
    }

    private static String buildTitle(Context context) {
        StringBuilder title = new StringBuilder();
        title.append("导出为Excel:").append(DATE_FORMAT.format(new Date()));
        return title.toString();
    }

    public interface MailListener {
        void startSend();

        void succeedSend();

        void failSend();

        void timeOut();

        void onFinish();

    }
}
