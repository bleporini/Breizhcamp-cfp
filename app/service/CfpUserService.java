package service;

import play.Application;
import scala.Option;
import securesocial.core.Identity;
import securesocial.core.IdentityId;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.Token;

import static play.Logger.info;

/**
 * Classe utilisée par SecureSocial pour la gestion des Identity
 *
 */
public class CfpUserService extends BaseUserService {

    private final BaseUserService delegate;

    public CfpUserService(Application application) {
        super(application);
        final Boolean toMock = application.configuration().getBoolean("mockUserService", false);
        info("Mocked user service : {}", toMock);
        delegate = toMock ?
                new MockCfpUserServiceDelegate(application):new CfpUserServiceDelegate(application);
    }

    public BaseUserService unWrap(){
        return delegate;
    }

    @Override
    public void deleteExpiredTokens() {
        delegate.deleteExpiredTokens();
    }

    @Override
    public void deleteToken(String uuid) {
        delegate.deleteToken(uuid);
    }

    @Override
    public void doDeleteExpiredTokens() {
        delegate.doDeleteExpiredTokens();
    }

    @Override
    public void doDeleteToken(String uuid) {
        delegate.doDeleteToken(uuid);
    }

    @Override
    public Identity doFind(IdentityId identityId) {
        return delegate.doFind(identityId);
    }

    @Override
    public Identity doFindByEmailAndProvider(String email, String providerId) {
        return delegate.doFindByEmailAndProvider(email, providerId);
    }

    @Override
    public Token doFindToken(String tokenId) {
        return delegate.doFindToken(tokenId);
    }

    @Override
    public void doSave(Token token) {
        delegate.doSave(token);
    }

    @Override
    public Identity doSave(Identity user) {
        return delegate.doSave(user);
    }

    @Override
    public Option<Identity> find(IdentityId id) {
        return delegate.find(id);
    }

    @Override
    public Option<Identity> findByEmailAndProvider(String email, String providerId) {
        return delegate.findByEmailAndProvider(email, providerId);
    }

    @Override
    public Option<securesocial.core.providers.Token> findToken(String token) {
        return delegate.findToken(token);
    }

    @Override
    public void save(securesocial.core.providers.Token token) {
        delegate.save(token);
    }

    @Override
    public Identity save(Identity user) {
        return delegate.save(user);
    }



}
