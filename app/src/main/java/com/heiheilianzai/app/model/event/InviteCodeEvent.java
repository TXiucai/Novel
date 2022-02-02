package com.heiheilianzai.app.model.event;

public class InviteCodeEvent {
    public boolean isInvite;
    public String inviteCode;

    public InviteCodeEvent(boolean isInvite, String inviteCode) {
        this.isInvite = isInvite;
        this.inviteCode = inviteCode;
    }
}
