package controllers;


import models.User;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Security;

import static play.libs.Json.toJson;

@Security.Authenticated(Secured.class)
public class UserRestController extends Controller {

    public static Result get() {
        User user = User.findByEmail(request().username());
        if (!user.admin) {
            return unauthorized();
        }
        return ok(toJson(User.findAll()));
    }

}
