package com.uol.yt120.lecampus.utility;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.uol.yt120.lecampus.domain.UserEvent;

import org.jetbrains.annotations.NotNull;

public class UserEventConverter implements WeekViewDisplayable<UserEvent> {

    @NotNull
    @Override
    public WeekViewEvent<UserEvent> toWeekViewEvent() {
        //return WeekViewEvent.Builder<UsesrEvent>();
        return null;
    }

}
