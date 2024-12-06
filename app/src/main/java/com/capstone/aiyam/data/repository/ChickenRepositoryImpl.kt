package com.capstone.aiyam.data.repository

import android.util.Log
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.reduceFileImage
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

val dummyClassifications = listOf(
    // October 27, 2024 (5 items)
    Classification(
        id = 1,
        mediaUrl = "https://example.com/media/1.jpg",
        deadChicken = false,
        createdAt = "2024-10-27T02:15:23.430Z",
        isAlert = true
    ),
    Classification(
        id = 2,
        mediaUrl = "https://example.com/media/2.jpg",
        deadChicken = true,
        createdAt = "2024-10-27T08:45:12.560Z",
        isAlert = false
    ),
    Classification(
        id = 3,
        mediaUrl = "https://example.com/media/3.jpg",
        deadChicken = false,
        createdAt = "2024-10-27T14:30:45.220Z",
        isAlert = null
    ),
    Classification(
        id = 4,
        mediaUrl = "https://example.com/media/4.jpg",
        deadChicken = true,
        createdAt = "2024-10-27T19:50:10.980Z",
        isAlert = true
    ),
    Classification(
        id = 5,
        mediaUrl = "https://example.com/media/5.jpg",
        deadChicken = false,
        createdAt = "2024-10-27T23:05:19.430Z",
        isAlert = false
    ),

    // October 28, 2024 (3 items)
    Classification(
        id = 6,
        mediaUrl = "https://example.com/media/6.jpg",
        deadChicken = true,
        createdAt = "2024-10-28T04:20:33.120Z",
        isAlert = null
    ),
    Classification(
        id = 7,
        mediaUrl = "https://example.com/media/7.jpg",
        deadChicken = false,
        createdAt = "2024-10-28T12:35:55.780Z",
        isAlert = true
    ),
    Classification(
        id = 8,
        mediaUrl = "https://example.com/media/8.jpg",
        deadChicken = true,
        createdAt = "2024-10-28T18:50:27.890Z",
        isAlert = false
    ),

    // October 29, 2024 (4 items)
    Classification(
        id = 9,
        mediaUrl = "https://example.com/media/9.jpg",
        deadChicken = false,
        createdAt = "2024-10-29T01:10:12.450Z",
        isAlert = null
    ),
    Classification(
        id = 10,
        mediaUrl = "https://example.com/media/10.jpg",
        deadChicken = true,
        createdAt = "2024-10-29T09:25:50.330Z",
        isAlert = true
    ),
    Classification(
        id = 11,
        mediaUrl = "https://example.com/media/11.jpg",
        deadChicken = false,
        createdAt = "2024-10-29T15:40:34.890Z",
        isAlert = false
    ),
    Classification(
        id = 12,
        mediaUrl = "https://example.com/media/12.jpg",
        deadChicken = true,
        createdAt = "2024-10-29T21:55:19.560Z",
        isAlert = null
    ),

    // October 30, 2024 (6 items)
    Classification(
        id = 13,
        mediaUrl = "https://example.com/media/13.jpg",
        deadChicken = false,
        createdAt = "2024-10-30T03:15:23.430Z",
        isAlert = true
    ),
    Classification(
        id = 14,
        mediaUrl = "https://example.com/media/14.jpg",
        deadChicken = true,
        createdAt = "2024-10-30T07:45:12.560Z",
        isAlert = false
    ),
    Classification(
        id = 15,
        mediaUrl = "https://example.com/media/15.jpg",
        deadChicken = false,
        createdAt = "2024-10-30T11:30:45.220Z",
        isAlert = null
    ),
    Classification(
        id = 16,
        mediaUrl = "https://example.com/media/16.jpg",
        deadChicken = true,
        createdAt = "2024-10-30T16:50:10.980Z",
        isAlert = true
    ),
    Classification(
        id = 17,
        mediaUrl = "https://example.com/media/17.jpg",
        deadChicken = false,
        createdAt = "2024-10-30T20:05:19.430Z",
        isAlert = false
    ),
    Classification(
        id = 18,
        mediaUrl = "https://example.com/media/18.jpg",
        deadChicken = true,
        createdAt = "2024-10-30T22:25:33.120Z",
        isAlert = null
    ),

    // October 31, 2024 (8 items)
    Classification(
        id = 19,
        mediaUrl = "https://example.com/media/19.jpg",
        deadChicken = false,
        createdAt = "2024-10-31T00:05:10.300Z",
        isAlert = true
    ),
    Classification(
        id = 20,
        mediaUrl = "https://example.com/media/20.jpg",
        deadChicken = true,
        createdAt = "2024-10-31T03:50:50.900Z",
        isAlert = false
    ),
    Classification(
        id = 21,
        mediaUrl = "https://example.com/media/21.jpg",
        deadChicken = false,
        createdAt = "2024-10-31T07:25:13.450Z",
        isAlert = null
    ),
    Classification(
        id = 22,
        mediaUrl = "https://example.com/media/22.jpg",
        deadChicken = true,
        createdAt = "2024-10-31T10:40:22.110Z",
        isAlert = true
    ),
    Classification(
        id = 23,
        mediaUrl = "https://example.com/media/23.jpg",
        deadChicken = false,
        createdAt = "2024-10-31T14:05:49.720Z",
        isAlert = false
    ),
    Classification(
        id = 24,
        mediaUrl = "https://example.com/media/24.jpg",
        deadChicken = true,
        createdAt = "2024-10-31T18:30:33.500Z",
        isAlert = null
    ),
    Classification(
        id = 25,
        mediaUrl = "https://example.com/media/25.jpg",
        deadChicken = false,
        createdAt = "2024-10-31T21:55:01.890Z",
        isAlert = true
    ),
    Classification(
        id = 26,
        mediaUrl = "https://example.com/media/26.jpg",
        deadChicken = true,
        createdAt = "2024-10-31T23:59:59.999Z",
        isAlert = false
    ),

    // November 01, 2024 (2 items)
    Classification(
        id = 27,
        mediaUrl = "https://example.com/media/27.jpg",
        deadChicken = false,
        createdAt = "2024-11-01T02:14:55.120Z",
        isAlert = null
    ),
    Classification(
        id = 28,
        mediaUrl = "https://example.com/media/28.jpg",
        deadChicken = true,
        createdAt = "2024-11-01T19:33:10.500Z",
        isAlert = true
    ),

    // November 02, 2024 (9 items)
    Classification(
        id = 29,
        mediaUrl = "https://example.com/media/29.jpg",
        deadChicken = false,
        createdAt = "2024-11-02T00:10:22.880Z",
        isAlert = false
    ),
    Classification(
        id = 30,
        mediaUrl = "https://example.com/media/30.jpg",
        deadChicken = true,
        createdAt = "2024-11-02T03:33:55.250Z",
        isAlert = null
    ),
    Classification(
        id = 31,
        mediaUrl = "https://example.com/media/31.jpg",
        deadChicken = false,
        createdAt = "2024-11-02T07:22:10.700Z",
        isAlert = true
    ),
    Classification(
        id = 32,
        mediaUrl = "https://example.com/media/32.jpg",
        deadChicken = true,
        createdAt = "2024-11-02T09:59:45.650Z",
        isAlert = false
    ),
    Classification(
        id = 33,
        mediaUrl = "https://example.com/media/33.jpg",
        deadChicken = false,
        createdAt = "2024-11-02T12:44:00.900Z",
        isAlert = null
    ),
    Classification(
        id = 34,
        mediaUrl = "https://example.com/media/34.jpg",
        deadChicken = true,
        createdAt = "2024-11-02T15:13:33.220Z",
        isAlert = true
    ),
    Classification(
        id = 35,
        mediaUrl = "https://example.com/media/35.jpg",
        deadChicken = false,
        createdAt = "2024-11-02T18:20:55.310Z",
        isAlert = false
    ),
    Classification(
        id = 36,
        mediaUrl = "https://example.com/media/36.jpg",
        deadChicken = true,
        createdAt = "2024-11-02T20:45:10.890Z",
        isAlert = null
    ),
    Classification(
        id = 37,
        mediaUrl = "https://example.com/media/37.jpg",
        deadChicken = false,
        createdAt = "2024-11-02T23:10:05.500Z",
        isAlert = true
    ),

    // November 03, 2024 (7 items)
    Classification(
        id = 38,
        mediaUrl = "https://example.com/media/38.jpg",
        deadChicken = true,
        createdAt = "2024-11-03T01:25:33.450Z",
        isAlert = false
    ),
    Classification(
        id = 39,
        mediaUrl = "https://example.com/media/39.jpg",
        deadChicken = false,
        createdAt = "2024-11-03T05:50:12.110Z",
        isAlert = null
    ),
    Classification(
        id = 40,
        mediaUrl = "https://example.com/media/40.jpg",
        deadChicken = true,
        createdAt = "2024-11-03T10:05:59.990Z",
        isAlert = true
    ),
    Classification(
        id = 41,
        mediaUrl = "https://example.com/media/41.jpg",
        deadChicken = false,
        createdAt = "2024-11-03T13:33:45.210Z",
        isAlert = false
    ),
    Classification(
        id = 42,
        mediaUrl = "https://example.com/media/42.jpg",
        deadChicken = true,
        createdAt = "2024-11-03T16:40:20.600Z",
        isAlert = null
    ),
    Classification(
        id = 43,
        mediaUrl = "https://example.com/media/43.jpg",
        deadChicken = false,
        createdAt = "2024-11-03T20:15:42.330Z",
        isAlert = true
    ),
    Classification(
        id = 44,
        mediaUrl = "https://example.com/media/44.jpg",
        deadChicken = true,
        createdAt = "2024-11-03T22:59:10.780Z",
        isAlert = false
    ),

    // November 04, 2024 (4 items)
    Classification(
        id = 45,
        mediaUrl = "https://example.com/media/45.jpg",
        deadChicken = false,
        createdAt = "2024-11-04T01:10:22.450Z",
        isAlert = true
    ),
    Classification(
        id = 46,
        mediaUrl = "https://example.com/media/46.jpg",
        deadChicken = true,
        createdAt = "2024-11-04T06:22:55.110Z",
        isAlert = false
    ),
    Classification(
        id = 47,
        mediaUrl = "https://example.com/media/47.jpg",
        deadChicken = false,
        createdAt = "2024-11-04T12:45:33.700Z",
        isAlert = null
    ),
    Classification(
        id = 48,
        mediaUrl = "https://example.com/media/48.jpg",
        deadChicken = true,
        createdAt = "2024-11-04T19:50:10.990Z",
        isAlert = true
    ),

    // November 05, 2024 (8 items)
    Classification(
        id = 49,
        mediaUrl = "https://example.com/media/49.jpg",
        deadChicken = false,
        createdAt = "2024-11-05T00:05:33.450Z",
        isAlert = false
    ),
    Classification(
        id = 50,
        mediaUrl = "https://example.com/media/50.jpg",
        deadChicken = true,
        createdAt = "2024-11-05T02:55:10.210Z",
        isAlert = null
    ),
    Classification(
        id = 51,
        mediaUrl = "https://example.com/media/51.jpg",
        deadChicken = false,
        createdAt = "2024-11-05T06:30:42.600Z",
        isAlert = true
    ),
    Classification(
        id = 52,
        mediaUrl = "https://example.com/media/52.jpg",
        deadChicken = true,
        createdAt = "2024-11-05T09:45:15.330Z",
        isAlert = false
    ),
    Classification(
        id = 53,
        mediaUrl = "https://example.com/media/53.jpg",
        deadChicken = false,
        createdAt = "2024-11-05T13:20:20.890Z",
        isAlert = null
    ),
    Classification(
        id = 54,
        mediaUrl = "https://example.com/media/54.jpg",
        deadChicken = true,
        createdAt = "2024-11-05T15:55:59.500Z",
        isAlert = true
    ),
    Classification(
        id = 55,
        mediaUrl = "https://example.com/media/55.jpg",
        deadChicken = false,
        createdAt = "2024-11-05T18:40:10.220Z",
        isAlert = false
    ),
    Classification(
        id = 56,
        mediaUrl = "https://example.com/media/56.jpg",
        deadChicken = true,
        createdAt = "2024-11-05T23:59:49.999Z",
        isAlert = null
    ),

    // November 06, 2024 (2 items)
    Classification(
        id = 57,
        mediaUrl = "https://example.com/media/57.jpg",
        deadChicken = false,
        createdAt = "2024-11-06T04:10:50.120Z",
        isAlert = true
    ),
    Classification(
        id = 58,
        mediaUrl = "https://example.com/media/58.jpg",
        deadChicken = true,
        createdAt = "2024-11-06T21:25:33.450Z",
        isAlert = false
    ),

    // November 07, 2024 (9 items)
    Classification(
        id = 59,
        mediaUrl = "https://example.com/media/59.jpg",
        deadChicken = false,
        createdAt = "2024-11-07T00:15:10.300Z",
        isAlert = null
    ),
    Classification(
        id = 60,
        mediaUrl = "https://example.com/media/60.jpg",
        deadChicken = true,
        createdAt = "2024-11-07T03:59:33.220Z",
        isAlert = true
    ),
    Classification(
        id = 61,
        mediaUrl = "https://example.com/media/61.jpg",
        deadChicken = false,
        createdAt = "2024-11-07T07:25:42.600Z",
        isAlert = false
    ),
    Classification(
        id = 62,
        mediaUrl = "https://example.com/media/62.jpg",
        deadChicken = true,
        createdAt = "2024-11-07T10:10:55.310Z",
        isAlert = null
    ),
    Classification(
        id = 63,
        mediaUrl = "https://example.com/media/63.jpg",
        deadChicken = false,
        createdAt = "2024-11-07T13:45:20.890Z",
        isAlert = true
    ),
    Classification(
        id = 64,
        mediaUrl = "https://example.com/media/64.jpg",
        deadChicken = true,
        createdAt = "2024-11-07T16:30:10.980Z",
        isAlert = false
    ),
    Classification(
        id = 65,
        mediaUrl = "https://example.com/media/65.jpg",
        deadChicken = false,
        createdAt = "2024-11-07T19:55:59.500Z",
        isAlert = null
    ),
    Classification(
        id = 66,
        mediaUrl = "https://example.com/media/66.jpg",
        deadChicken = true,
        createdAt = "2024-11-07T21:44:33.210Z",
        isAlert = true
    ),
    Classification(
        id = 67,
        mediaUrl = "https://example.com/media/67.jpg",
        deadChicken = false,
        createdAt = "2024-11-07T23:59:10.780Z",
        isAlert = false
    ),

    // November 08, 2024 (3 items)
    Classification(
        id = 68,
        mediaUrl = "https://example.com/media/68.jpg",
        deadChicken = true,
        createdAt = "2024-11-08T02:25:33.450Z",
        isAlert = null
    ),
    Classification(
        id = 69,
        mediaUrl = "https://example.com/media/69.jpg",
        deadChicken = false,
        createdAt = "2024-11-08T08:00:22.110Z",
        isAlert = true
    ),
    Classification(
        id = 70,
        mediaUrl = "https://example.com/media/70.jpg",
        deadChicken = true,
        createdAt = "2024-11-08T16:59:59.999Z",
        isAlert = false
    ),

    // November 09, 2024 (5 items)
    Classification(
        id = 71,
        mediaUrl = "https://example.com/media/71.jpg",
        deadChicken = false,
        createdAt = "2024-11-09T00:10:22.880Z",
        isAlert = null
    ),
    Classification(
        id = 72,
        mediaUrl = "https://example.com/media/72.jpg",
        deadChicken = true,
        createdAt = "2024-11-09T05:33:55.250Z",
        isAlert = true
    ),
    Classification(
        id = 73,
        mediaUrl = "https://example.com/media/73.jpg",
        deadChicken = false,
        createdAt = "2024-11-09T12:22:10.700Z",
        isAlert = false
    ),
    Classification(
        id = 74,
        mediaUrl = "https://example.com/media/74.jpg",
        deadChicken = true,
        createdAt = "2024-11-09T18:59:45.650Z",
        isAlert = null
    ),
    Classification(
        id = 75,
        mediaUrl = "https://example.com/media/75.jpg",
        deadChicken = false,
        createdAt = "2024-11-09T23:44:00.900Z",
        isAlert = true
    ),

    // November 10, 2024 (7 items)
    Classification(
        id = 76,
        mediaUrl = "https://example.com/media/76.jpg",
        deadChicken = true,
        createdAt = "2024-11-10T01:13:33.220Z",
        isAlert = false
    ),
    Classification(
        id = 77,
        mediaUrl = "https://example.com/media/77.jpg",
        deadChicken = false,
        createdAt = "2024-11-10T04:20:55.310Z",
        isAlert = null
    ),
    Classification(
        id = 78,
        mediaUrl = "https://example.com/media/78.jpg",
        deadChicken = true,
        createdAt = "2024-11-10T08:45:10.890Z",
        isAlert = true
    ),
    Classification(
        id = 79,
        mediaUrl = "https://example.com/media/79.jpg",
        deadChicken = false,
        createdAt = "2024-11-10T11:10:05.500Z",
        isAlert = false
    ),
    Classification(
        id = 80,
        mediaUrl = "https://example.com/media/80.jpg",
        deadChicken = true,
        createdAt = "2024-11-10T14:25:33.450Z",
        isAlert = null
    ),
    Classification(
        id = 81,
        mediaUrl = "https://example.com/media/81.jpg",
        deadChicken = false,
        createdAt = "2024-11-10T19:50:12.110Z",
        isAlert = true
    ),
    Classification(
        id = 82,
        mediaUrl = "https://example.com/media/82.jpg",
        deadChicken = true,
        createdAt = "2024-11-10T22:59:10.780Z",
        isAlert = false
    ),

    // November 11, 2024 (8 items)
    Classification(
        id = 83,
        mediaUrl = "https://example.com/media/83.jpg",
        deadChicken = false,
        createdAt = "2024-11-11T00:05:10.300Z",
        isAlert = null
    ),
    Classification(
        id = 84,
        mediaUrl = "https://example.com/media/84.jpg",
        deadChicken = true,
        createdAt = "2024-11-11T03:33:45.220Z",
        isAlert = true
    ),
    Classification(
        id = 85,
        mediaUrl = "https://example.com/media/85.jpg",
        deadChicken = false,
        createdAt = "2024-11-11T07:10:22.450Z",
        isAlert = false
    ),
    Classification(
        id = 86,
        mediaUrl = "https://example.com/media/86.jpg",
        deadChicken = true,
        createdAt = "2024-11-11T10:59:59.999Z",
        isAlert = null
    ),
    Classification(
        id = 87,
        mediaUrl = "https://example.com/media/87.jpg",
        deadChicken = false,
        createdAt = "2024-11-11T13:50:50.900Z",
        isAlert = true
    ),
    Classification(
        id = 88,
        mediaUrl = "https://example.com/media/88.jpg",
        deadChicken = true,
        createdAt = "2024-11-11T17:25:13.450Z",
        isAlert = false
    ),
    Classification(
        id = 89,
        mediaUrl = "https://example.com/media/89.jpg",
        deadChicken = false,
        createdAt = "2024-11-11T20:40:22.110Z",
        isAlert = null
    ),
    Classification(
        id = 90,
        mediaUrl = "https://example.com/media/90.jpg",
        deadChicken = true,
        createdAt = "2024-11-11T23:55:01.890Z",
        isAlert = true
    ),

    // November 12, 2024 (2 items)
    Classification(
        id = 91,
        mediaUrl = "https://example.com/media/91.jpg",
        deadChicken = false,
        createdAt = "2024-11-12T02:14:55.120Z",
        isAlert = false
    ),
    Classification(
        id = 92,
        mediaUrl = "https://example.com/media/92.jpg",
        deadChicken = true,
        createdAt = "2024-11-12T19:33:10.500Z",
        isAlert = null
    ),

    // November 13, 2024 (9 items)
    Classification(
        id = 93,
        mediaUrl = "https://example.com/media/93.jpg",
        deadChicken = false,
        createdAt = "2024-11-13T00:10:22.880Z",
        isAlert = true
    ),
    Classification(
        id = 94,
        mediaUrl = "https://example.com/media/94.jpg",
        deadChicken = true,
        createdAt = "2024-11-13T03:33:55.250Z",
        isAlert = false
    ),
    Classification(
        id = 95,
        mediaUrl = "https://example.com/media/95.jpg",
        deadChicken = false,
        createdAt = "2024-11-13T07:22:10.700Z",
        isAlert = null
    ),
    Classification(
        id = 96,
        mediaUrl = "https://example.com/media/96.jpg",
        deadChicken = true,
        createdAt = "2024-11-13T09:59:45.650Z",
        isAlert = true
    ),
    Classification(
        id = 97,
        mediaUrl = "https://example.com/media/97.jpg",
        deadChicken = false,
        createdAt = "2024-11-13T12:44:00.900Z",
        isAlert = false
    ),
    Classification(
        id = 98,
        mediaUrl = "https://example.com/media/98.jpg",
        deadChicken = true,
        createdAt = "2024-11-13T15:13:33.220Z",
        isAlert = null
    ),
    Classification(
        id = 99,
        mediaUrl = "https://example.com/media/99.jpg",
        deadChicken = false,
        createdAt = "2024-11-13T18:20:55.310Z",
        isAlert = true
    ),
    Classification(
        id = 100,
        mediaUrl = "https://example.com/media/100.jpg",
        deadChicken = true,
        createdAt = "2024-11-13T20:45:10.890Z",
        isAlert = false
    ),
    Classification(
        id = 101,
        mediaUrl = "https://example.com/media/101.jpg",
        deadChicken = false,
        createdAt = "2024-11-13T23:10:05.500Z",
        isAlert = null
    ),

    // November 14, 2024 (4 items)
    Classification(
        id = 102,
        mediaUrl = "https://example.com/media/102.jpg",
        deadChicken = true,
        createdAt = "2024-11-14T01:25:33.450Z",
        isAlert = true
    ),
    Classification(
        id = 103,
        mediaUrl = "https://example.com/media/103.jpg",
        deadChicken = false,
        createdAt = "2024-11-14T05:50:12.110Z",
        isAlert = false
    ),
    Classification(
        id = 104,
        mediaUrl = "https://example.com/media/104.jpg",
        deadChicken = true,
        createdAt = "2024-11-14T10:05:59.990Z",
        isAlert = null
    ),
    Classification(
        id = 105,
        mediaUrl = "https://example.com/media/105.jpg",
        deadChicken = false,
        createdAt = "2024-11-14T22:59:10.780Z",
        isAlert = true
    ),

    // November 15, 2024 (8 items)
    Classification(
        id = 106,
        mediaUrl = "https://example.com/media/106.jpg",
        deadChicken = true,
        createdAt = "2024-11-15T00:05:10.300Z",
        isAlert = false
    ),
    Classification(
        id = 107,
        mediaUrl = "https://example.com/media/107.jpg",
        deadChicken = false,
        createdAt = "2024-11-15T03:50:50.900Z",
        isAlert = null
    ),
    Classification(
        id = 108,
        mediaUrl = "https://example.com/media/108.jpg",
        deadChicken = true,
        createdAt = "2024-11-15T07:25:13.450Z",
        isAlert = true
    ),
    Classification(
        id = 109,
        mediaUrl = "https://example.com/media/109.jpg",
        deadChicken = false,
        createdAt = "2024-11-15T10:40:22.110Z",
        isAlert = false
    ),
    Classification(
        id = 110,
        mediaUrl = "https://example.com/media/110.jpg",
        deadChicken = true,
        createdAt = "2024-11-15T14:05:49.720Z",
        isAlert = null
    ),
    Classification(
        id = 111,
        mediaUrl = "https://example.com/media/111.jpg",
        deadChicken = false,
        createdAt = "2024-11-15T18:30:33.500Z",
        isAlert = true
    ),
    Classification(
        id = 112,
        mediaUrl = "https://example.com/media/112.jpg",
        deadChicken = true,
        createdAt = "2024-11-15T21:55:01.890Z",
        isAlert = false
    ),
    Classification(
        id = 113,
        mediaUrl = "https://example.com/media/113.jpg",
        deadChicken = false,
        createdAt = "2024-11-15T23:59:59.999Z",
        isAlert = null
    ),

    // November 16, 2024 (3 items)
    Classification(
        id = 114,
        mediaUrl = "https://example.com/media/114.jpg",
        deadChicken = true,
        createdAt = "2024-11-16T02:14:55.120Z",
        isAlert = true
    ),
    Classification(
        id = 115,
        mediaUrl = "https://example.com/media/115.jpg",
        deadChicken = false,
        createdAt = "2024-11-16T12:33:10.500Z",
        isAlert = false
    ),
    Classification(
        id = 116,
        mediaUrl = "https://example.com/media/116.jpg",
        deadChicken = true,
        createdAt = "2024-11-16T19:33:10.500Z",
        isAlert = null
    ),

    // November 17, 2024 (6 items)
    Classification(
        id = 117,
        mediaUrl = "https://example.com/media/117.jpg",
        deadChicken = false,
        createdAt = "2024-11-17T01:10:22.450Z",
        isAlert = true
    ),
    Classification(
        id = 118,
        mediaUrl = "https://example.com/media/118.jpg",
        deadChicken = true,
        createdAt = "2024-11-17T06:22:55.110Z",
        isAlert = false
    ),
    Classification(
        id = 119,
        mediaUrl = "https://example.com/media/119.jpg",
        deadChicken = false,
        createdAt = "2024-11-17T12:45:33.700Z",
        isAlert = null
    ),
    Classification(
        id = 120,
        mediaUrl = "https://example.com/media/120.jpg",
        deadChicken = true,
        createdAt = "2024-11-17T15:50:10.990Z",
        isAlert = true
    ),
    Classification(
        id = 121,
        mediaUrl = "https://example.com/media/121.jpg",
        deadChicken = false,
        createdAt = "2024-11-17T20:05:33.450Z",
        isAlert = false
    ),
    Classification(
        id = 122,
        mediaUrl = "https://example.com/media/122.jpg",
        deadChicken = true,
        createdAt = "2024-11-17T23:44:59.500Z",
        isAlert = null
    ),

    // November 18, 2024 (9 items)
    Classification(
        id = 123,
        mediaUrl = "https://example.com/media/123.jpg",
        deadChicken = false,
        createdAt = "2024-11-18T00:05:33.450Z",
        isAlert = true
    ),
    Classification(
        id = 124,
        mediaUrl = "https://example.com/media/124.jpg",
        deadChicken = true,
        createdAt = "2024-11-18T02:55:10.210Z",
        isAlert = false
    ),
    Classification(
        id = 125,
        mediaUrl = "https://example.com/media/125.jpg",
        deadChicken = false,
        createdAt = "2024-11-18T06:30:42.600Z",
        isAlert = null
    ),
    Classification(
        id = 126,
        mediaUrl = "https://example.com/media/126.jpg",
        deadChicken = true,
        createdAt = "2024-11-18T09:45:15.330Z",
        isAlert = true
    ),
    Classification(
        id = 127,
        mediaUrl = "https://example.com/media/127.jpg",
        deadChicken = false,
        createdAt = "2024-11-18T13:20:20.890Z",
        isAlert = false
    ),
    Classification(
        id = 128,
        mediaUrl = "https://example.com/media/128.jpg",
        deadChicken = true,
        createdAt = "2024-11-18T15:55:59.500Z",
        isAlert = null
    ),
    Classification(
        id = 129,
        mediaUrl = "https://example.com/media/129.jpg",
        deadChicken = false,
        createdAt = "2024-11-18T18:40:10.220Z",
        isAlert = true
    ),
    Classification(
        id = 130,
        mediaUrl = "https://example.com/media/130.jpg",
        deadChicken = true,
        createdAt = "2024-11-18T20:25:49.999Z",
        isAlert = false
    ),
    Classification(
        id = 131,
        mediaUrl = "https://example.com/media/131.jpg",
        deadChicken = false,
        createdAt = "2024-11-18T23:59:59.999Z",
        isAlert = null
    ),

    // November 19, 2024 (5 items)
    Classification(
        id = 132,
        mediaUrl = "https://example.com/media/132.jpg",
        deadChicken = true,
        createdAt = "2024-11-19T02:14:55.120Z",
        isAlert = true
    ),
    Classification(
        id = 133,
        mediaUrl = "https://example.com/media/133.jpg",
        deadChicken = false,
        createdAt = "2024-11-19T04:33:10.500Z",
        isAlert = false
    ),
    Classification(
        id = 134,
        mediaUrl = "https://example.com/media/134.jpg",
        deadChicken = true,
        createdAt = "2024-11-19T09:10:22.450Z",
        isAlert = null
    ),
    Classification(
        id = 135,
        mediaUrl = "https://example.com/media/135.jpg",
        deadChicken = false,
        createdAt = "2024-11-19T16:59:59.999Z",
        isAlert = true
    ),
    Classification(
        id = 136,
        mediaUrl = "https://example.com/media/136.jpg",
        deadChicken = true,
        createdAt = "2024-11-19T21:40:50.900Z",
        isAlert = false
    ),

    // November 20, 2024 (2 items)
    Classification(
        id = 137,
        mediaUrl = "https://example.com/media/137.jpg",
        deadChicken = false,
        createdAt = "2024-11-20T00:05:10.300Z",
        isAlert = null
    ),
    Classification(
        id = 138,
        mediaUrl = "https://example.com/media/138.jpg",
        deadChicken = true,
        createdAt = "2024-11-20T23:59:59.999Z",
        isAlert = true
    ),

    // November 21, 2024 (8 items)
    Classification(
        id = 139,
        mediaUrl = "https://example.com/media/139.jpg",
        deadChicken = false,
        createdAt = "2024-11-21T01:13:33.220Z",
        isAlert = false
    ),
    Classification(
        id = 140,
        mediaUrl = "https://example.com/media/140.jpg",
        deadChicken = true,
        createdAt = "2024-11-21T04:20:55.310Z",
        isAlert = null
    ),
    Classification(
        id = 141,
        mediaUrl = "https://example.com/media/141.jpg",
        deadChicken = false,
        createdAt = "2024-11-21T08:45:10.890Z",
        isAlert = true
    ),
    Classification(
        id = 142,
        mediaUrl = "https://example.com/media/142.jpg",
        deadChicken = true,
        createdAt = "2024-11-21T11:10:05.500Z",
        isAlert = false
    ),
    Classification(
        id = 143,
        mediaUrl = "https://example.com/media/143.jpg",
        deadChicken = false,
        createdAt = "2024-11-21T14:25:33.450Z",
        isAlert = null
    ),
    Classification(
        id = 144,
        mediaUrl = "https://example.com/media/144.jpg",
        deadChicken = true,
        createdAt = "2024-11-21T18:50:12.110Z",
        isAlert = true
    ),
    Classification(
        id = 145,
        mediaUrl = "https://example.com/media/145.jpg",
        deadChicken = false,
        createdAt = "2024-11-21T20:59:10.780Z",
        isAlert = false
    ),
    Classification(
        id = 146,
        mediaUrl = "https://example.com/media/146.jpg",
        deadChicken = true,
        createdAt = "2024-11-21T23:45:33.450Z",
        isAlert = null
    ),

    // November 22, 2024 (4 items)
    Classification(
        id = 147,
        mediaUrl = "https://example.com/media/147.jpg",
        deadChicken = false,
        createdAt = "2024-11-22T02:25:33.450Z",
        isAlert = true
    ),
    Classification(
        id = 148,
        mediaUrl = "https://example.com/media/148.jpg",
        deadChicken = true,
        createdAt = "2024-11-22T08:00:22.110Z",
        isAlert = false
    ),
    Classification(
        id = 149,
        mediaUrl = "https://example.com/media/149.jpg",
        deadChicken = false,
        createdAt = "2024-11-22T16:59:59.999Z",
        isAlert = null
    ),
    Classification(
        id = 150,
        mediaUrl = "https://example.com/media/150.jpg",
        deadChicken = true,
        createdAt = "2024-11-22T23:59:59.000Z",
        isAlert = true
    ),

    // November 23, 2024 (9 items)
    Classification(
        id = 151,
        mediaUrl = "https://example.com/media/151.jpg",
        deadChicken = false,
        createdAt = "2024-11-23T00:10:22.880Z",
        isAlert = false
    ),
    Classification(
        id = 152,
        mediaUrl = "https://example.com/media/152.jpg",
        deadChicken = true,
        createdAt = "2024-11-23T03:33:55.250Z",
        isAlert = null
    ),
    Classification(
        id = 153,
        mediaUrl = "https://example.com/media/153.jpg",
        deadChicken = false,
        createdAt = "2024-11-23T07:22:10.700Z",
        isAlert = true
    ),
    Classification(
        id = 154,
        mediaUrl = "https://example.com/media/154.jpg",
        deadChicken = true,
        createdAt = "2024-11-23T09:59:45.650Z",
        isAlert = false
    ),
    Classification(
        id = 155,
        mediaUrl = "https://example.com/media/155.jpg",
        deadChicken = false,
        createdAt = "2024-11-23T12:44:00.900Z",
        isAlert = null
    ),
    Classification(
        id = 156,
        mediaUrl = "https://example.com/media/156.jpg",
        deadChicken = true,
        createdAt = "2024-11-23T15:13:33.220Z",
        isAlert = true
    ),
    Classification(
        id = 157,
        mediaUrl = "https://example.com/media/157.jpg",
        deadChicken = false,
        createdAt = "2024-11-23T18:20:55.310Z",
        isAlert = false
    ),
    Classification(
        id = 158,
        mediaUrl = "https://example.com/media/158.jpg",
        deadChicken = true,
        createdAt = "2024-11-23T20:45:10.890Z",
        isAlert = null
    ),
    Classification(
        id = 159,
        mediaUrl = "https://example.com/media/159.jpg",
        deadChicken = false,
        createdAt = "2024-11-23T23:10:05.500Z",
        isAlert = true
    ),

    // November 24, 2024 (7 items)
    Classification(
        id = 160,
        mediaUrl = "https://example.com/media/160.jpg",
        deadChicken = true,
        createdAt = "2024-11-24T01:25:33.450Z",
        isAlert = false
    ),
    Classification(
        id = 161,
        mediaUrl = "https://example.com/media/161.jpg",
        deadChicken = false,
        createdAt = "2024-11-24T05:50:12.110Z",
        isAlert = null
    ),
    Classification(
        id = 162,
        mediaUrl = "https://example.com/media/162.jpg",
        deadChicken = true,
        createdAt = "2024-11-24T10:05:59.990Z",
        isAlert = true
    ),
    Classification(
        id = 163,
        mediaUrl = "https://example.com/media/163.jpg",
        deadChicken = false,
        createdAt = "2024-11-24T13:33:45.210Z",
        isAlert = false
    ),
    Classification(
        id = 164,
        mediaUrl = "https://example.com/media/164.jpg",
        deadChicken = true,
        createdAt = "2024-11-24T16:40:20.600Z",
        isAlert = null
    ),
    Classification(
        id = 165,
        mediaUrl = "https://example.com/media/165.jpg",
        deadChicken = false,
        createdAt = "2024-11-24T20:15:42.330Z",
        isAlert = true
    ),
    Classification(
        id = 166,
        mediaUrl = "https://example.com/media/166.jpg",
        deadChicken = true,
        createdAt = "2024-11-24T22:59:10.780Z",
        isAlert = false
    ),

    // November 25, 2024 (3 items)
    Classification(
        id = 167,
        mediaUrl = "https://example.com/media/167.jpg",
        deadChicken = false,
        createdAt = "2024-11-25T00:15:10.300Z",
        isAlert = null
    ),
    Classification(
        id = 168,
        mediaUrl = "https://example.com/media/168.jpg",
        deadChicken = true,
        createdAt = "2024-11-25T05:33:45.220Z",
        isAlert = true
    ),
    Classification(
        id = 169,
        mediaUrl = "https://example.com/media/169.jpg",
        deadChicken = false,
        createdAt = "2024-11-25T11:10:22.450Z",
        isAlert = false
    ),

    // November 26, 2024 (5 items)
    Classification(
        id = 170,
        mediaUrl = "https://example.com/media/170.jpg",
        deadChicken = true,
        createdAt = "2024-11-26T03:59:59.999Z",
        isAlert = null
    ),
    Classification(
        id = 171,
        mediaUrl = "https://example.com/media/171.jpg",
        deadChicken = false,
        createdAt = "2024-11-26T07:25:13.450Z",
        isAlert = true
    ),
    Classification(
        id = 172,
        mediaUrl = "https://example.com/media/172.jpg",
        deadChicken = true,
        createdAt = "2024-11-26T10:40:22.110Z",
        isAlert = false
    ),
    Classification(
        id = 173,
        mediaUrl = "https://example.com/media/173.jpg",
        deadChicken = false,
        createdAt = "2024-11-26T14:05:49.720Z",
        isAlert = null
    ),
    Classification(
        id = 174,
        mediaUrl = "https://example.com/media/174.jpg",
        deadChicken = true,
        createdAt = "2024-11-26T21:55:01.890Z",
        isAlert = true
    ),

    // November 27, 2024 (2 items)
    Classification(
        id = 175,
        mediaUrl = "https://example.com/media/175.jpg",
        deadChicken = false,
        createdAt = "2024-11-27T02:14:55.120Z",
        isAlert = false
    ),
    Classification(
        id = 176,
        mediaUrl = "https://example.com/media/176.jpg",
        deadChicken = true,
        createdAt = "2024-11-27T19:33:10.500Z",
        isAlert = null
    ),

    // November 28, 2024 (8 items)
    Classification(
        id = 177,
        mediaUrl = "https://example.com/media/177.jpg",
        deadChicken = false,
        createdAt = "2024-11-28T00:10:22.880Z",
        isAlert = true
    ),
    Classification(
        id = 178,
        mediaUrl = "https://example.com/media/178.jpg",
        deadChicken = true,
        createdAt = "2024-11-28T03:33:55.250Z",
        isAlert = false
    ),
    Classification(
        id = 179,
        mediaUrl = "https://example.com/media/179.jpg",
        deadChicken = false,
        createdAt = "2024-11-28T07:22:10.700Z",
        isAlert = null
    ),
    Classification(
        id = 180,
        mediaUrl = "https://example.com/media/180.jpg",
        deadChicken = true,
        createdAt = "2024-11-28T09:59:45.650Z",
        isAlert = true
    ),
    Classification(
        id = 181,
        mediaUrl = "https://example.com/media/181.jpg",
        deadChicken = false,
        createdAt = "2024-11-28T12:44:00.900Z",
        isAlert = false
    ),
    Classification(
        id = 182,
        mediaUrl = "https://example.com/media/182.jpg",
        deadChicken = true,
        createdAt = "2024-11-28T15:13:33.220Z",
        isAlert = null
    ),
    Classification(
        id = 183,
        mediaUrl = "https://example.com/media/183.jpg",
        deadChicken = false,
        createdAt = "2024-11-28T18:20:55.310Z",
        isAlert = true
    ),
    Classification(
        id = 184,
        mediaUrl = "https://example.com/media/184.jpg",
        deadChicken = true,
        createdAt = "2024-11-28T20:45:10.890Z",
        isAlert = false
    ),

    // November 29, 2024 (4 items)
    Classification(
        id = 185,
        mediaUrl = "https://example.com/media/185.jpg",
        deadChicken = false,
        createdAt = "2024-11-29T01:25:33.450Z",
        isAlert = null
    ),
    Classification(
        id = 186,
        mediaUrl = "https://example.com/media/186.jpg",
        deadChicken = true,
        createdAt = "2024-11-29T05:50:12.110Z",
        isAlert = true
    ),
    Classification(
        id = 187,
        mediaUrl = "https://example.com/media/187.jpg",
        deadChicken = false,
        createdAt = "2024-11-29T10:05:59.990Z",
        isAlert = false
    ),
    Classification(
        id = 188,
        mediaUrl = "https://example.com/media/188.jpg",
        deadChicken = true,
        createdAt = "2024-11-29T22:59:10.780Z",
        isAlert = null
    ),

    // November 30, 2024 (6 items)
    Classification(
        id = 189,
        mediaUrl = "https://example.com/media/189.jpg",
        deadChicken = false,
        createdAt = "2024-11-30T00:05:10.300Z",
        isAlert = true
    ),
    Classification(
        id = 190,
        mediaUrl = "https://example.com/media/190.jpg",
        deadChicken = true,
        createdAt = "2024-11-30T03:33:45.220Z",
        isAlert = false
    ),
    Classification(
        id = 191,
        mediaUrl = "https://example.com/media/191.jpg",
        deadChicken = false,
        createdAt = "2024-11-30T07:10:22.450Z",
        isAlert = null
    ),
    Classification(
        id = 192,
        mediaUrl = "https://example.com/media/192.jpg",
        deadChicken = true,
        createdAt = "2024-11-30T10:59:59.999Z",
        isAlert = true
    ),
    Classification(
        id = 193,
        mediaUrl = "https://example.com/media/193.jpg",
        deadChicken = false,
        createdAt = "2024-11-30T13:50:50.900Z",
        isAlert = false
    ),
    Classification(
        id = 194,
        mediaUrl = "https://example.com/media/194.jpg",
        deadChicken = true,
        createdAt = "2024-11-30T17:25:13.450Z",
        isAlert = null
    ),

    // December 01, 2024 (3 items)
    Classification(
        id = 195,
        mediaUrl = "https://example.com/media/195.jpg",
        deadChicken = false,
        createdAt = "2024-12-01T02:14:55.120Z",
        isAlert = true
    ),
    Classification(
        id = 196,
        mediaUrl = "https://example.com/media/196.jpg",
        deadChicken = true,
        createdAt = "2024-12-01T09:10:22.450Z",
        isAlert = false
    ),
    Classification(
        id = 197,
        mediaUrl = "https://example.com/media/197.jpg",
        deadChicken = false,
        createdAt = "2024-12-01T20:33:10.500Z",
        isAlert = null
    ),

    // December 02, 2024 (9 items)
    Classification(
        id = 198,
        mediaUrl = "https://example.com/media/198.jpg",
        deadChicken = true,
        createdAt = "2024-12-02T00:10:22.880Z",
        isAlert = true
    ),
    Classification(
        id = 199,
        mediaUrl = "https://example.com/media/199.jpg",
        deadChicken = false,
        createdAt = "2024-12-02T03:33:55.250Z",
        isAlert = false
    ),
    Classification(
        id = 200,
        mediaUrl = "https://example.com/media/200.jpg",
        deadChicken = true,
        createdAt = "2024-12-02T07:22:10.700Z",
        isAlert = null
    ),
    Classification(
        id = 201,
        mediaUrl = "https://example.com/media/201.jpg",
        deadChicken = false,
        createdAt = "2024-12-02T09:59:45.650Z",
        isAlert = true
    ),
    Classification(
        id = 202,
        mediaUrl = "https://example.com/media/202.jpg",
        deadChicken = true,
        createdAt = "2024-12-02T12:44:00.900Z",
        isAlert = false
    ),
    Classification(
        id = 203,
        mediaUrl = "https://example.com/media/203.jpg",
        deadChicken = false,
        createdAt = "2024-12-02T15:13:33.220Z",
        isAlert = null
    ),
    Classification(
        id = 204,
        mediaUrl = "https://example.com/media/204.jpg",
        deadChicken = true,
        createdAt = "2024-12-02T18:20:55.310Z",
        isAlert = true
    ),
    Classification(
        id = 205,
        mediaUrl = "https://example.com/media/205.jpg",
        deadChicken = false,
        createdAt = "2024-12-02T20:45:10.890Z",
        isAlert = false
    ),
    Classification(
        id = 206,
        mediaUrl = "https://example.com/media/206.jpg",
        deadChicken = true,
        createdAt = "2024-12-02T23:10:05.500Z",
        isAlert = null
    )
)

class ChickenRepositoryImpl @Inject constructor(
    private val chickenService: ChickenService,
    private val userRepository: UserRepository
) : ChickenRepository {
    private val user = userRepository.getFirebaseUser()

    override fun classifyChicken(file: File, mediaType: String): Flow<ResponseWrapper<Classification>> = flow {
        emit(ResponseWrapper.Loading)

        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val multipartBody =  MultipartBody.Part.createFormData("file", file.name, requestBody)

        try {
            val chicken = withToken(user, userRepository::getFirebaseToken) {
                chickenService.postChicken(it, multipartBody)
            }
            emit(ResponseWrapper.Success(chicken.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getHistories(): Flow<ResponseWrapper<List<Classification>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val histories = withToken(user, userRepository::getFirebaseToken) {
                chickenService.getHistories(it)
            }
            emit(ResponseWrapper.Success(histories.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
