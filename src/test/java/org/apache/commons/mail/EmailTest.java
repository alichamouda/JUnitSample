package org.apache.commons.mail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.Wiser;

import javax.mail.Session;
import javax.mail.internet.MimeMultipart;
import java.util.Date;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class EmailTest {


    private Email email;
    Wiser wiser;
    private  String defaultHostname= "localhost";
    private  String defaultPort= "2500";

    @Before
    public void setUp() throws Exception {
        this.email = new SimpleEmail();
        this.wiser = new Wiser();
        wiser.setHostname(this.defaultHostname);
        wiser.setPort(Integer.parseInt(this.defaultPort));
        this.wiser.start();
    }

    @After
    public void tearDown() throws Exception {
        this.email = null;
        this.wiser.stop();
    }

    @Test
    public void updateContentType() {
        this.email.setCharset("utf-8");
        this.email.updateContentType("text/plain");
        assertEquals( "text/plain; charset=UTF-8" ,this.email.contentType);
        assertEquals( "UTF-8" ,this.email.charset);
        this.email.updateContentType("text/html; charset=utf-8");
        assertEquals( "text/html; charset=utf-8" ,this.email.contentType);
        assertEquals( "utf-8" ,this.email.charset);
        this.email.updateContentType("");
        assertNull(this.email.contentType);
    }

    @Test
    public void getMailSession() throws EmailException {
        try {
            this.email.getMailSession();
            fail("Should fail for invalid hostname");
        }catch(EmailException aExp){
            assert(aExp.getMessage().contains("Cannot find valid hostname for mail session"));
        }
        this.email.hostName = "example.com";
        this.email.bounceAddress = "test.com";
        this.email.setSSLOnConnect(true);
        this.email.setSSLCheckServerIdentity(true);
        assertThat(this.email.getMailSession(), instanceOf(Session.class));
    }

    @Test
    public void setFrom() throws EmailException {

        try {
            // Fails for wrong address format
            Email emailSetted = this.email.setFrom("emnago.com");
            fail("Should fail for invalid email");
        }catch(EmailException ignored){
            assertTrue(true);
        }

        Email emailSetted = this.email.setFrom("emna@go.com");
        assertEquals("emna@go.com",emailSetted.fromAddress.getAddress());
        assertEquals(null,emailSetted.fromAddress.getPersonal());
    }

    @Test
    public void addCc() throws EmailException {
        try {
            // Fails for wrong address format
            Email emailSetted = this.email.addCc("emnago.com");
            fail("Should fail for invalid email");
        }catch(EmailException ignored){
            assertTrue(true);
        }
        try {
            // Fails for wrong address format
            Email emailSetted = this.email.addCc("");
            fail("Should fail for invalid email");
        }catch(EmailException aExp){
            assertTrue(true);
        }
        Email emailSetted = this.email.addCc("emna@go.com");
        assertEquals("emna@go.com",emailSetted.ccList.get(emailSetted.ccList.size()-1).getAddress());
    }

    @Test
    public void addBcc() throws EmailException {
        try {
            // Fails for wrong address format
            Email emailSetted = this.email.addBcc("emna@go.com","kson8tgo.com", "ksont3@go.com","ksont5@go.com");
            fail("Should fail for invalid email");
        }catch(EmailException ignored){
            assertTrue(true);
        }
        try {
            // Fails for wrong address format
            Email emailSetted = this.email.addBcc();
            fail("Should fail for empty array");
        }catch(EmailException ignored){
            assertTrue(true);
        }
        Email emailSetted = this.email.addBcc("emna@go.com","kson8@tgo.com", "ksont3@go.com","ksont5@go.com");
        assertEquals("ksont5@go.com",emailSetted.bccList.get(emailSetted.bccList.size()-1).getAddress());
    }

    @Test
    public void addReplyTo() throws EmailException {

        try {
            // Fails for wrong address format
            Email emailSetted = this.email.addReplyTo("emnago.com", "emna");
            fail("Should fail for invalid email");
        }catch(EmailException ignored){
            assertTrue(true);
        }

        Email emailSetted = this.email.addReplyTo("emna@go.com", "");
        assertEquals("emna@go.com",emailSetted.replyList.get(emailSetted.replyList.size()-1).getAddress());
        this.email.charset = "UTF-8";
        emailSetted = this.email.addReplyTo("emna@go.com", "emna");
        assertEquals("emna@go.com",emailSetted.replyList.get(emailSetted.replyList.size()-1).getAddress());
        assertEquals("emna",emailSetted.replyList.get(emailSetted.replyList.size()-1).getPersonal());
    }

    @Test
    public void addHeader() {

        try {
            // Fails for wrong address format
            this.email.addHeader("", "val");
            fail("Should fail for invalid header name");
        }catch(IllegalArgumentException ignored){
            assertTrue(true);
        }

        try {
            // Fails for wrong address format
            this.email.addHeader("pop", "");
            fail("Should fail for invalid header val");
        }catch(IllegalArgumentException ignored){
            assertTrue(true);
        }
        this.email.addHeader("test", "val");
       assertEquals("val", this.email.headers.get("test"));
    }

    @Test
    public void buildMimeMessage() throws EmailException {
        this.email.hostName = "example.com";
        this.email.message =null;
        this.email.bounceAddress = "test.com";
        this.email.setSSLOnConnect(true);
        this.email.setSSLCheckServerIdentity(true);
        this.email.subject = "test subject";
        this.email.charset = "UTF-8";
        this.email.updateContentType("text/plain; charset=UTF-8");
        this.email.addHeader("test", "val");
        this.email.addReplyTo("emna@go.com", "emna");
        this.email.addBcc("emna@go.com","kson8@tgo.com", "ksont3@go.com","ksont5@go.com");
        this.email.addCc("emna@go.com");
        this.email.emailBody = new MimeMultipart();
        this.email.content = "Hi";
        this.email.buildMimeMessage();
        assertTrue(true);
        try {
            // Fails for wrong address format
            this.email.buildMimeMessage();
            fail("Should fail. message is already built.");
        }catch(IllegalStateException ignored){
            assertTrue(true);
        }
        this.email.message =null;
        this.email.charset = "";
        this.email.content = null;
        this.email.buildMimeMessage();
        assertTrue(true);
    }

    @Test
    public void send() throws EmailException {
        try {
            this.email.hostName = this.defaultHostname;
            this.email.smtpPort = this.defaultPort;
            this.email.message =null;
            this.email.bounceAddress = "test.com";
            this.email.setSSLOnConnect(false);
            this.email.setSSLCheckServerIdentity(false);

            this.email.subject = "test subject";
            this.email.charset = "UTF-8";
            this.email.updateContentType("text/plain; charset=UTF-8");
            this.email.addHeader("test", "val");
            this.email.addReplyTo("to@localhost", "emna");
            this.email.addBcc("emna@go.com","kson8@tgo.com", "ksont3@go.com","ksont5@go.com");
            this.email.addCc("emna@go.com");
            this.email.emailBody = new MimeMultipart();
            this.email.content = "Hi";

            String result = this.email.send();
        }catch (EmailException exc) {
            fail("Could not send message");
        }
    }

    @Test
    public void getSentDate() {
        Date date = this.email.getSentDate();
        assertEquals(new Date(), date);
        this.email.sentDate =  new Date();
        date = this.email.getSentDate();
        assertEquals(this.email.sentDate, date);
    }

    @Test
    public void getHostName() throws EmailException {
        this.email.hostName = "example.com";
        assertEquals("example.com", this.email.getHostName());
        this.email.hostName = null;
        assertNull(this.email.getHostName());
        this.email.hostName = "example2.com";
        this.email.bounceAddress = "test.com";
        this.email.setSSLOnConnect(true);
        this.email.setSSLCheckServerIdentity(true);
        this.email.getMailSession();
        assertEquals("example2.com", this.email.getHostName());
    }

    @Test
    public void getSocketConnectionTimeout() {
        assertEquals(60000,this.email.getSocketConnectionTimeout());
        this.email.setSocketConnectionTimeout(20000);
        assertEquals(20000,this.email.getSocketConnectionTimeout());
    }
}