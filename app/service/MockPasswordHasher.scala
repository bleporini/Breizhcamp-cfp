package service

import securesocial.core.{Registry, PasswordInfo}
import securesocial.core.providers.utils.PasswordHasher

/**
 * @author blep
 *
 *
 */
class MockPasswordHasher(application: play.api.Application) extends PasswordHasher{
  override def id = "nocheck"

  def hash(plainPassword: String) = PasswordInfo(id,plainPassword)

  def matches(passwordInfo: PasswordInfo, suppliedPassword: String) = true

  override def onStart() = Registry.hashers.get(id).orElse {
      super.onStart()
      None
    }
}
