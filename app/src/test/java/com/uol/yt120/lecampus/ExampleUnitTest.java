package com.uol.yt120.lecampus;

import com.uol.yt120.lecampus.model.dataAccessObjects.UserEventDAO;
import com.uol.yt120.lecampus.model.domain.UserEvent;
import com.uol.yt120.lecampus.repository.UserEventRepository;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTimeFormatToText() {
        DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
        Date dateForTest = new Date(System.currentTimeMillis() - 60001); // 1 min ago
        String result = dateTimeCalculator.getTimeFormatOfText(dateForTest);
        assertEquals("1 minute ago", result);
    }
}