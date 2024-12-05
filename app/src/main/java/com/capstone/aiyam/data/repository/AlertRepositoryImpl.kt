package com.capstone.aiyam.data.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.UpdateAlertRequest
import com.capstone.aiyam.data.remote.AlertService
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.repository.AlertRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

val dummyAlerts = listOf(
    // October 27, 2024 - 5 alerts
    Alerts(1, "https://example.com/media/1", "2024-10-27T02:15:23.430Z", false),
    Alerts(2, "https://example.com/media/2", "2024-10-27T08:45:12.560Z", true),
    Alerts(3, "https://example.com/media/3", "2024-10-27T14:30:45.220Z", false),
    Alerts(4, "https://example.com/media/4", "2024-10-27T19:50:10.980Z", true),
    Alerts(5, "https://example.com/media/5", "2024-10-27T23:05:19.430Z", false),

    // October 28, 2024 - 3 alerts
    Alerts(6, "https://example.com/media/6", "2024-10-28T04:20:33.120Z", true),
    Alerts(7, "https://example.com/media/7", "2024-10-28T12:35:55.780Z", false),
    Alerts(8, "https://example.com/media/8", "2024-10-28T18:50:27.890Z", true),

    // October 29, 2024 - 4 alerts
    Alerts(9, "https://example.com/media/9", "2024-10-29T01:10:12.450Z", false),
    Alerts(10, "https://example.com/media/10", "2024-10-29T09:25:50.330Z", true),
    Alerts(11, "https://example.com/media/11", "2024-10-29T15:40:34.890Z", false),
    Alerts(12, "https://example.com/media/12", "2024-10-29T21:55:19.560Z", true),

    // October 30, 2024 - 6 alerts
    Alerts(13, "https://example.com/media/13", "2024-10-30T03:15:23.430Z", false),
    Alerts(14, "https://example.com/media/14", "2024-10-30T07:45:12.560Z", true),
    Alerts(15, "https://example.com/media/15", "2024-10-30T11:30:45.220Z", false),
    Alerts(16, "https://example.com/media/16", "2024-10-30T16:50:10.980Z", true),
    Alerts(17, "https://example.com/media/17", "2024-10-30T20:05:19.430Z", false),
    Alerts(18, "https://example.com/media/18", "2024-10-30T22:25:33.120Z", true),

    // October 31, 2024 - 7 alerts
    Alerts(19, "https://example.com/media/19", "2024-10-31T02:20:33.430Z", false),
    Alerts(20, "https://example.com/media/20", "2024-10-31T06:35:12.560Z", true),
    Alerts(21, "https://example.com/media/21", "2024-10-31T10:50:45.220Z", false),
    Alerts(22, "https://example.com/media/22", "2024-10-31T14:15:10.980Z", true),
    Alerts(23, "https://example.com/media/23", "2024-10-31T18:30:19.430Z", false),
    Alerts(24, "https://example.com/media/24", "2024-10-31T21:45:55.780Z", true),
    Alerts(25, "https://example.com/media/25", "2024-10-31T23:59:27.890Z", false),

    // November 1, 2024 - 5 alerts
    Alerts(26, "https://example.com/media/26", "2024-11-01T01:10:12.450Z", true),
    Alerts(27, "https://example.com/media/27", "2024-11-01T05:25:50.330Z", false),
    Alerts(28, "https://example.com/media/28", "2024-11-01T09:40:34.890Z", true),
    Alerts(29, "https://example.com/media/29", "2024-11-01T13:55:19.560Z", false),
    Alerts(30, "https://example.com/media/30", "2024-11-01T18:10:33.120Z", true),

    // November 2, 2024 - 4 alerts
    Alerts(31, "https://example.com/media/31", "2024-11-02T02:20:33.430Z", false),
    Alerts(32, "https://example.com/media/32", "2024-11-02T07:35:12.560Z", true),
    Alerts(33, "https://example.com/media/33", "2024-11-02T12:50:45.220Z", false),
    Alerts(34, "https://example.com/media/34", "2024-11-02T17:15:10.980Z", true),

    // November 3, 2024 - 3 alerts
    Alerts(35, "https://example.com/media/35", "2024-11-03T03:25:23.430Z", false),
    Alerts(36, "https://example.com/media/36", "2024-11-03T08:45:12.560Z", true),
    Alerts(37, "https://example.com/media/37", "2024-11-03T14:30:45.220Z", false),

    // November 4, 2024 - 6 alerts
    Alerts(38, "https://example.com/media/38", "2024-11-04T01:15:23.430Z", true),
    Alerts(39, "https://example.com/media/39", "2024-11-04T05:45:12.560Z", false),
    Alerts(40, "https://example.com/media/40", "2024-11-04T10:30:45.220Z", true),
    Alerts(41, "https://example.com/media/41", "2024-11-04T15:50:10.980Z", false),
    Alerts(42, "https://example.com/media/42", "2024-11-04T19:05:19.430Z", true),
    Alerts(43, "https://example.com/media/43", "2024-11-04T22:25:33.120Z", false),

    // November 5, 2024 - 5 alerts
    Alerts(44, "https://example.com/media/44", "2024-11-05T02:20:33.430Z", true),
    Alerts(45, "https://example.com/media/45", "2024-11-05T06:35:12.560Z", false),
    Alerts(46, "https://example.com/media/46", "2024-11-05T11:50:45.220Z", true),
    Alerts(47, "https://example.com/media/47", "2024-11-05T16:15:10.980Z", false),
    Alerts(48, "https://example.com/media/48", "2024-11-05T20:30:19.430Z", true),

    // November 6, 2024 - 4 alerts
    Alerts(49, "https://example.com/media/49", "2024-11-06T03:10:12.450Z", false),
    Alerts(50, "https://example.com/media/50", "2024-11-06T08:25:50.330Z", true),
    Alerts(51, "https://example.com/media/51", "2024-11-06T13:40:34.890Z", false),
    Alerts(52, "https://example.com/media/52", "2024-11-06T18:55:19.560Z", true),
    // November 7, 2024 - 3 alerts
    Alerts(53, "https://example.com/media/53", "2024-11-07T01:15:23.430Z", false),
    Alerts(54, "https://example.com/media/54", "2024-11-07T07:45:12.560Z", true),
    Alerts(55, "https://example.com/media/55", "2024-11-07T12:30:45.220Z", false),

    // November 8, 2024 - 6 alerts
    Alerts(56, "https://example.com/media/56", "2024-11-08T02:20:33.430Z", true),
    Alerts(57, "https://example.com/media/57", "2024-11-08T08:35:12.560Z", false),
    Alerts(58, "https://example.com/media/58", "2024-11-08T14:50:45.220Z", true),
    Alerts(59, "https://example.com/media/59", "2024-11-08T19:05:10.980Z", false),
    Alerts(60, "https://example.com/media/60", "2024-11-08T21:20:19.430Z", true),
    Alerts(61, "https://example.com/media/61", "2024-11-08T23:35:33.120Z", false),

    // November 9, 2024 - 4 alerts
    Alerts(62, "https://example.com/media/62", "2024-11-09T03:25:23.430Z", true),
    Alerts(63, "https://example.com/media/63", "2024-11-09T09:45:12.560Z", false),
    Alerts(64, "https://example.com/media/64", "2024-11-09T15:30:45.220Z", true),
    Alerts(65, "https://example.com/media/65", "2024-11-09T20:50:10.980Z", false),

    // November 10, 2024 - 5 alerts
    Alerts(66, "https://example.com/media/66", "2024-11-10T04:10:12.450Z", true),
    Alerts(67, "https://example.com/media/67", "2024-11-10T10:25:50.330Z", false),
    Alerts(68, "https://example.com/media/68", "2024-11-10T16:40:34.890Z", true),
    Alerts(69, "https://example.com/media/69", "2024-11-10T21:55:19.560Z", false),
    Alerts(70, "https://example.com/media/70", "2024-11-10T23:10:33.120Z", true),

    // November 11, 2024 - 3 alerts
    Alerts(71, "https://example.com/media/71", "2024-11-11T02:20:33.430Z", false),
    Alerts(72, "https://example.com/media/72", "2024-11-11T07:35:12.560Z", true),
    Alerts(73, "https://example.com/media/73", "2024-11-11T12:50:45.220Z", false),

    // November 12, 2024 - 6 alerts
    Alerts(74, "https://example.com/media/74", "2024-11-12T01:15:23.430Z", true),
    Alerts(75, "https://example.com/media/75", "2024-11-12T06:45:12.560Z", false),
    Alerts(76, "https://example.com/media/76", "2024-11-12T11:30:45.220Z", true),
    Alerts(77, "https://example.com/media/77", "2024-11-12T16:50:10.980Z", false),
    Alerts(78, "https://example.com/media/78", "2024-11-12T20:05:19.430Z", true),
    Alerts(79, "https://example.com/media/79", "2024-11-12T22:25:33.120Z", false),

    // November 13, 2024 - 4 alerts
    Alerts(80, "https://example.com/media/80", "2024-11-13T03:20:33.430Z", true),
    Alerts(81, "https://example.com/media/81", "2024-11-13T08:35:12.560Z", false),
    Alerts(82, "https://example.com/media/82", "2024-11-13T13:50:45.220Z", true),
    Alerts(83, "https://example.com/media/83", "2024-11-13T19:05:10.980Z", false),

    // November 14, 2024 - 5 alerts
    Alerts(84, "https://example.com/media/84", "2024-11-14T04:10:12.450Z", true),
    Alerts(85, "https://example.com/media/85", "2024-11-14T09:25:50.330Z", false),
    Alerts(86, "https://example.com/media/86", "2024-11-14T14:40:34.890Z", true),
    Alerts(87, "https://example.com/media/87", "2024-11-14T19:55:19.560Z", false),
    Alerts(88, "https://example.com/media/88", "2024-11-14T23:10:33.120Z", true),

    // November 15, 2024 - 3 alerts
    Alerts(89, "https://example.com/media/89", "2024-11-15T02:20:33.430Z", false),
    Alerts(90, "https://example.com/media/90", "2024-11-15T07:35:12.560Z", true),
    Alerts(91, "https://example.com/media/91", "2024-11-15T12:50:45.220Z", false),

    // November 16, 2024 - 6 alerts
    Alerts(92, "https://example.com/media/92", "2024-11-16T01:15:23.430Z", true),
    Alerts(93, "https://example.com/media/93", "2024-11-16T06:45:12.560Z", false),
    Alerts(94, "https://example.com/media/94", "2024-11-16T11:30:45.220Z", true),
    Alerts(95, "https://example.com/media/95", "2024-11-16T16:50:10.980Z", false),
    Alerts(96, "https://example.com/media/96", "2024-11-16T20:05:19.430Z", true),
    Alerts(97, "https://example.com/media/97", "2024-11-16T22:25:33.120Z", false),

    // November 17, 2024 - 4 alerts
    Alerts(98, "https://example.com/media/98", "2024-11-17T03:20:33.430Z", true),
    Alerts(99, "https://example.com/media/99", "2024-11-17T08:35:12.560Z", false),
    Alerts(100, "https://example.com/media/100", "2024-11-17T13:50:45.220Z", true),
    Alerts(101, "https://example.com/media/101", "2024-11-17T19:05:10.980Z", false),
    // November 18, 2024 - 5 alerts
    Alerts(102, "https://example.com/media/102", "2024-11-18T04:10:12.450Z", true),
    Alerts(103, "https://example.com/media/103", "2024-11-18T09:25:50.330Z", false),
    Alerts(104, "https://example.com/media/104", "2024-11-18T14:40:34.890Z", true),
    Alerts(105, "https://example.com/media/105", "2024-11-18T19:55:19.560Z", false),
    Alerts(106, "https://example.com/media/106", "2024-11-18T23:10:33.120Z", true),

    // November 19, 2024 - 3 alerts
    Alerts(107, "https://example.com/media/107", "2024-11-19T02:20:33.430Z", false),
    Alerts(108, "https://example.com/media/108", "2024-11-19T07:35:12.560Z", true),
    Alerts(109, "https://example.com/media/109", "2024-11-19T12:50:45.220Z", false),

    // November 20, 2024 - 6 alerts
    Alerts(110, "https://example.com/media/110", "2024-11-20T01:15:23.430Z", true),
    Alerts(111, "https://example.com/media/111", "2024-11-20T06:45:12.560Z", false),
    Alerts(112, "https://example.com/media/112", "2024-11-20T11:30:45.220Z", true),
    Alerts(113, "https://example.com/media/113", "2024-11-20T16:50:10.980Z", false),
    Alerts(114, "https://example.com/media/114", "2024-11-20T20:05:19.430Z", true),
    Alerts(115, "https://example.com/media/115", "2024-11-20T22:25:33.120Z", false),

    // November 21, 2024 - 4 alerts
    Alerts(116, "https://example.com/media/116", "2024-11-21T03:20:33.430Z", true),
    Alerts(117, "https://example.com/media/117", "2024-11-21T08:35:12.560Z", false),
    Alerts(118, "https://example.com/media/118", "2024-11-21T13:50:45.220Z", true),
    Alerts(119, "https://example.com/media/119", "2024-11-21T19:05:10.980Z", false),

    // November 22, 2024 - 5 alerts
    Alerts(120, "https://example.com/media/120", "2024-11-22T04:10:12.450Z", true),
    Alerts(121, "https://example.com/media/121", "2024-11-22T09:25:50.330Z", false),
    Alerts(122, "https://example.com/media/122", "2024-11-22T14:40:34.890Z", true),
    Alerts(123, "https://example.com/media/123", "2024-11-22T19:55:19.560Z", false),
    Alerts(124, "https://example.com/media/124", "2024-11-22T23:10:33.120Z", true),

    // November 23, 2024 - 3 alerts
    Alerts(125, "https://example.com/media/125", "2024-11-23T02:20:33.430Z", false),
    Alerts(126, "https://example.com/media/126", "2024-11-23T07:35:12.560Z", true),
    Alerts(127, "https://example.com/media/127", "2024-11-23T12:50:45.220Z", false),

    // November 24, 2024 - 6 alerts
    Alerts(128, "https://example.com/media/128", "2024-11-24T01:15:23.430Z", true),
    Alerts(129, "https://example.com/media/129", "2024-11-24T06:45:12.560Z", false),
    Alerts(130, "https://example.com/media/130", "2024-11-24T11:30:45.220Z", true),
    Alerts(131, "https://example.com/media/131", "2024-11-24T16:50:10.980Z", false),
    Alerts(132, "https://example.com/media/132", "2024-11-24T20:05:19.430Z", true),
    Alerts(133, "https://example.com/media/133", "2024-11-24T22:25:33.120Z", false),

    // November 25, 2024 - 4 alerts
    Alerts(134, "https://example.com/media/134", "2024-11-25T03:20:33.430Z", true),
    Alerts(135, "https://example.com/media/135", "2024-11-25T08:35:12.560Z", false),
    Alerts(136, "https://example.com/media/136", "2024-11-25T13:50:45.220Z", true),
    Alerts(137, "https://example.com/media/137", "2024-11-25T19:05:10.980Z", false),

    // November 26, 2024 - 5 alerts
    Alerts(138, "https://example.com/media/138", "2024-11-26T04:10:12.450Z", true),
    Alerts(139, "https://example.com/media/139", "2024-11-26T09:25:50.330Z", false),
    Alerts(140, "https://example.com/media/140", "2024-11-26T14:40:34.890Z", true),
    Alerts(141, "https://example.com/media/141", "2024-11-26T19:55:19.560Z", false),
    Alerts(142, "https://example.com/media/142", "2024-11-26T23:10:33.120Z", true),

    // November 27, 2024 - 3 alerts
    Alerts(143, "https://example.com/media/143", "2024-11-27T02:20:33.430Z", false),
    Alerts(144, "https://example.com/media/144", "2024-11-27T07:35:12.560Z", true),
    Alerts(145, "https://example.com/media/145", "2024-11-27T12:50:45.220Z", false),

    // November 28, 2024 - 6 alerts
    Alerts(146, "https://example.com/media/146", "2024-11-28T01:15:23.430Z", true),
    Alerts(147, "https://example.com/media/147", "2024-11-28T06:45:12.560Z", false),
    Alerts(148, "https://example.com/media/148", "2024-11-28T11:30:45.220Z", true),
    Alerts(149, "https://example.com/media/149", "2024-11-28T16:50:10.980Z", false),
    Alerts(150, "https://example.com/media/150", "2024-11-28T20:05:19.430Z", true),
    Alerts(151, "https://example.com/media/151", "2024-11-28T22:25:33.120Z", false),

    // November 29, 2024 - 4 alerts
    Alerts(152, "https://example.com/media/152", "2024-11-29T03:20:33.430Z", true),
    Alerts(153, "https://example.com/media/153", "2024-11-29T08:35:12.560Z", false),
    Alerts(154, "https://example.com/media/154", "2024-11-29T13:50:45.220Z", true),
    Alerts(155, "https://example.com/media/155", "2024-11-29T19:05:10.980Z", false),

    // November 30, 2024 - 5 alerts
    Alerts(156, "https://example.com/media/156", "2024-11-30T04:10:12.450Z", true),
    Alerts(157, "https://example.com/media/157", "2024-11-30T09:25:50.330Z", false),
    Alerts(158, "https://example.com/media/158", "2024-11-30T14:40:34.890Z", true),
    Alerts(159, "https://example.com/media/159", "2024-11-30T19:55:19.560Z", false),
    Alerts(160, "https://example.com/media/160", "2024-11-30T23:10:33.120Z", true),

    // December 1, 2024 - 3 alerts
    Alerts(161, "https://example.com/media/161", "2024-12-01T02:20:33.430Z", false),
    Alerts(162, "https://example.com/media/162", "2024-12-01T07:35:12.560Z", true),
    Alerts(163, "https://example.com/media/163", "2024-12-01T12:50:45.220Z", false),

    // December 2, 2024 - 4 alerts
    Alerts(164, "https://example.com/media/164", "2024-12-02T03:20:33.430Z", true),
    Alerts(165, "https://example.com/media/165", "2024-12-02T08:35:12.560Z", false),
    Alerts(166, "https://example.com/media/166", "2024-12-02T13:50:45.220Z", true),
    Alerts(167, "https://example.com/media/167", "2024-12-02T19:05:10.980Z", false),

    Alerts(168, "https://example.com/media/168", "2024-12-02T04:10:12.450Z", true),
    Alerts(169, "https://example.com/media/169", "2024-12-02T09:25:50.330Z", false),
    Alerts(170, "https://example.com/media/170", "2024-12-02T14:40:34.890Z", true),
    Alerts(171, "https://example.com/media/171", "2024-12-02T19:55:19.560Z", false),
    Alerts(172, "https://example.com/media/172", "2024-12-02T23:10:33.120Z", true),
    Alerts(173, "https://example.com/media/173", "2024-12-02T01:15:23.430Z", false),
    Alerts(174, "https://example.com/media/174", "2024-12-02T06:45:12.560Z", true),
    Alerts(175, "https://example.com/media/175", "2024-12-02T11:30:45.220Z", false),
    Alerts(176, "https://example.com/media/176", "2024-12-02T16:50:10.980Z", true),
    Alerts(177, "https://example.com/media/177", "2024-12-02T20:05:19.430Z", false),
    Alerts(178, "https://example.com/media/178", "2024-12-02T22:25:33.120Z", true),
    Alerts(179, "https://example.com/media/179", "2024-12-02T03:20:33.430Z", false),
    Alerts(180, "https://example.com/media/180", "2024-12-02T08:35:12.560Z", true),
    Alerts(181, "https://example.com/media/181", "2024-12-02T13:50:45.220Z", false),
    Alerts(182, "https://example.com/media/182", "2024-12-02T19:05:10.980Z", true),
    Alerts(183, "https://example.com/media/183", "2024-12-02T23:10:33.120Z", false),
    Alerts(184, "https://example.com/media/184", "2024-12-02T04:10:12.450Z", true),
    Alerts(185, "https://example.com/media/185", "2024-12-02T09:25:50.330Z", false),
    Alerts(186, "https://example.com/media/186", "2024-12-02T14:40:34.890Z", true),
    Alerts(187, "https://example.com/media/187", "2024-12-02T19:55:19.560Z", false),
    Alerts(188, "https://example.com/media/188", "2024-12-02T23:10:33.120Z", true),
    Alerts(189, "https://example.com/media/189", "2024-12-02T01:15:23.430Z", false),
    Alerts(190, "https://example.com/media/190", "2024-12-02T06:45:12.560Z", true),
    Alerts(191, "https://example.com/media/191", "2024-12-02T11:30:45.220Z", false),
    Alerts(192, "https://example.com/media/192", "2024-12-02T16:50:10.980Z", true),
    Alerts(193, "https://example.com/media/193", "2024-12-02T20:05:19.430Z", false),
    Alerts(194, "https://example.com/media/194", "2024-12-02T22:25:33.120Z", true)
)

class AlertRepositoryImpl @Inject constructor(
    private val alertService: AlertService,
    private val userRepository: UserRepository
) : AlertRepository {
    private val user = userRepository.getFirebaseUser()

    override fun getAlerts(): Flow<ResponseWrapper<List<Alerts>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val alerts = withToken(user, userRepository::getFirebaseToken) {
                alertService.getAlerts(it)
            }
            emit(ResponseWrapper.Success(dummyAlerts.reversed()))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getAlertById(id: Int): Flow<ResponseWrapper<Alerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val alert = withToken(user, userRepository::getFirebaseToken) {
                val updateRequest = UpdateAlertRequest(isRead = true)
                alertService.updateAlertById(it, id, updateRequest)
            }
            emit(ResponseWrapper.Success(alert.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
