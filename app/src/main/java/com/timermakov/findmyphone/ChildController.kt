package com.timermakov.findmyphone

import retrofit2.Call
import retrofit2.http.*

interface ChildController {
    @POST("/hello")
    fun hello(@Body user: User): Call<Boolean>

    @GET("/list")
    fun list(): Call<List<User>>

    @GET("/get/{firstName}/{lastName}")
    fun get(@Path("firstName") firstName:String , @Path("lastName") lastName: String ): Call<User>

}
