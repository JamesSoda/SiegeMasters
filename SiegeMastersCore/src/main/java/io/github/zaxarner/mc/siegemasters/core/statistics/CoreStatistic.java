package io.github.zaxarner.mc.siegemasters.core.statistics;

/**
 * Created on 5/19/2020.
 * CoreStatistic contains all of the Statistics that are handled within the Core plugin. However, a statistic is
 * identified by just a string so sub-plugins can define and handle their own statistics.
 */

public enum CoreStatistic {
    VOTES,
    DONATED_AMOUNT,
    EXP,
    KICKS,
    MUTES,
    BANS,
    TIME_PLAYED
}
