package com.bornewtech.marketplacepesaing.data.response

data class DirectionResponse(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val steps: List<Step>
)

data class OverviewPolyline(
    val points: String
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Step(
    val html_instructions: String,
    val distance: Distance,
    val duration: Duration,
    val start_location: Location,
    val end_location: Location,
    val polyline: Polyline
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Polyline(
    val points: String
)
