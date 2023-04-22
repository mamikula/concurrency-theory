package org.example;

import org.junit.Assert;
import org.junit.Test;


public class WyscigTest {

    @Test
    public void wyscigStartTest() {
        Wyscig dobryWyscig = new Wyscig(new SemaforBinarny(true));
        Assert.assertEquals(0, dobryWyscig.wyscigStart());


        Wyscig zepsutyWyscig = new Wyscig(new ZepsutySemaforBinarny(true));
        Assert.assertNotEquals(0, zepsutyWyscig.wyscigStart());

    }


}