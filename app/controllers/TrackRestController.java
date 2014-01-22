package controllers;

import models.Proposal;
import models.StatusProposal;
import models.Track;
import models.User;
import models.utils.TransformValidationErrors;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;

import java.util.List;

import static play.data.Form.form;
import static play.libs.Json.toJson;


@SecureSocial.SecuredAction(ajaxCall = true)
public class TrackRestController extends BaseController {

    public static Result get(Long id) {
        Track track = Track.find.byId(id);
        if (track == null) {
            return noContent();
        }
        return ok(toJson(track));
    }



    public static Result getProposals(Long id) {
        Track track = Track.find.byId(id);
        if (track == null) {
            return noContent();
        }

        StatusProposal statusProposal = StatusProposal.ACCEPTED;

        List<Proposal> proposals = Proposal.findByTrackAndStatus(track, statusProposal);
        for (Proposal proposal : proposals) {
            proposal.fiteredCoSpeakers();
        }
        return ok(toJson(proposals));
    }

    public static Result all() {
        return ok(toJson(Track.find.all()));
    }

    public static Result delete(Long id) {

        // Vérification du rôle d'admin
        User user = getLoggedUser();
        if (!user.admin) {
            return forbidden();
        }

        Track track = Track.find.byId(id);
        if (track != null) {
            track.delete();
        }
        // HTTP 204 en cas de succès (NO CONTENT)
        return noContent();
    }

    public static Result save() {

        // Vérification du rôle d'admin
        User user = getLoggedUser();
        if (!user.admin) {
            return forbidden();
        }

        Form<Track> trackForm = form(Track.class).bindFromRequest();

        if (trackForm.hasErrors()) {
            return badRequest(toJson(TransformValidationErrors.transform(trackForm.errors())));
        }

        Track formTrack = trackForm.get();

        if (formTrack.getId() == null) {
            // Nouveau Track
            if (Track.findByTitle(formTrack.getTitle()) != null) {
                return badRequest(toJson(TransformValidationErrors.transform(Messages.get("error.track.already.exist"))));
            }

            formTrack.save();
        } else {
            // Mise à jour d'un track
            Track dbTrack = Track.find.byId(formTrack.getId());
            if (!formTrack.getTitle().equals(dbTrack.getTitle())
                    && Track.findByTitle(formTrack.getTitle()) != null) {
                return badRequest(toJson(TransformValidationErrors.transform(Messages.get("error.track.already.exist"))));
            }

            dbTrack.setDescription(formTrack.getDescription());
            dbTrack.update();
        }

        // HTTP 204 en cas de succès (NO CONTENT)
        return noContent();
    }
}
