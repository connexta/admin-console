package org.codice.ddf.admin.query.commons;

import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.ActionHandlerType;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.ActionType;

public abstract class DefaultActionHandler implements ActionHandlerType{

//    @Override
//    public ActionReport process(ActionType action, List<Field> args) {
//        if(action == null) {
//            // TODO: tbatie - 2/6/17 - Return default unknown action message
//            return null;
//        }
//
//        Optional<ActionType> foundAction = getSupportedActions().stream()
//                .filter(actionType -> actionType.getActionId()
//                        .getUniqueName()
//                        .equals(action.getActionId()
//                                .getUniqueName()))
//                .findFirst();
//
//        if(foundAction.isPresent()) {
//            return foundAction.get().setArguments(args).process();
//        }
//        // TODO: tbatie - 2/6/17 - Return default unknown action message
//        return null;
//    }
}
