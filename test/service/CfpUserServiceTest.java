package service;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import play.Configuration;
import play.GlobalSettings;
import play.test.FakeApplication;
import play.test.Helpers;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.BaseUserService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static service.MockCfpUserServiceDelegate.defaultIdentity;

/**
 * User: blep
 * Date: 23/01/14
 * Time: 08:30
 */

public class CfpUserServiceTest {

    static {
        System.setProperty("config.resource", "dev-local.conf");
    }

    private final Map<String, Object> conf = buildConf();

    private Map<String,Object> buildConf(){
        Map<String, Object> conf = new HashMap<>();
        conf.put("mockUserService", true);
        return conf;
    }

    private final FakeApplication fakeApplication = fakeApplication(conf);
    private final CfpUserService userService = fakeApplication.getWrappedApplication().plugin(CfpUserService.class).get();

    @Test
    public void should_return_default_user() throws Exception {
        final FakeApplication fakeApplication = Helpers.fakeApplication();
        running(fakeApplication, new Runnable() {
            @Override
            public void run() {
                final Identity identity = userService.doFind(new IdentityId("test","icule"));
                assertThat(identity).isNotNull();
                assertThat(identity).isEqualTo(defaultIdentity);
            }
        });
    }

    @Test
    public void should_not_save_user_twice() throws Exception {
        running(fakeApplication, new Runnable() {
            @Override
            public void run() {
                final Identity identity = defaultIdentity;
                assertThat(userService.doSave(identity)).isNotNull()
                    .isEqualTo(defaultIdentity);
                assertThat(userService.doSave(identity)).isNotNull()
                    .isEqualTo(defaultIdentity);

            }
        });
    }

    @Test
    public void should_not_mock() throws Exception {
        Map<String, Object> conf = new HashMap<>();
        conf.put("mockUserService", false);
        final FakeApplication app = fakeApplication(conf);
        running(app, new Runnable() {
            @Override
            public void run() {
                final BaseUserService userService1 = app.getWrappedApplication()
                        .plugin(CfpUserService.class).get().unWrap();
                assertThat(userService1.getClass()).isEqualTo(CfpUserServiceDelegate.class);
                assertThat(userService1.getClass()).isNotEqualTo(MockCfpUserServiceDelegate.class);
            }
        });
    }

    @Test
    public void should_mock() throws Exception {
        running(fakeApplication, new Runnable() {
            @Override
            public void run() {
                final BaseUserService delegate = userService.unWrap();
                assertThat(delegate.getClass()).isEqualTo(MockCfpUserServiceDelegate.class);
                assertThat(delegate.getClass()).isNotEqualTo(CfpUserServiceDelegate.class);
            }
        });
    }
}
