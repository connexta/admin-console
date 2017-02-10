package org.codice.ddf.admin.query.sample;

import org.codice.ddf.admin.query.api.ActionMessage;

public class SampleActionMessage implements ActionMessage{
    @Override
    public MessageType getMessageType() {
        return MessageType.SUCCESS;
    }

    @Override
    public String getCode() {
        return "SAMPLE_SUCCESS";
    }

    @Override
    public String getDescription() {
        return "This is a sample success message.";
    }
}
