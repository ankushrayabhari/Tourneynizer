package com.tourneynizer.tourneynizer.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.tourneynizer.tourneynizer.services.HTTPService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CookieRequestFactoryTest {

    private static final Map<String, String> HEADER = new HashMap<>();

    private static CookieRequestFactory factory;

    @BeforeClass
    public static void createFactory() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getTargetContext();
        HTTPService.init(context);
        HttpCookie cookie = new HttpCookie("session", "12345");
        HEADER.put("Cookie", cookie.toString());
        HTTPService.getInstance().getCookieManager().getCookieStore().add(new URI(HTTPService.DOMAIN), cookie);
        factory = new CookieRequestFactory();
    }

    @Test
    public void testMakeStringRequest() {
        StringRequest request = factory.makeStringRequest(Request.Method.GET, HTTPService.DOMAIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        assertTrue(request != null);
        try {
            assertEquals(HEADER, request.getHeaders());
        } catch (AuthFailureError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testMakeJsonObjectRequest() {
        JsonObjectRequest request = factory.makeJsonObjectRequest(Request.Method.GET, HTTPService.DOMAIN, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        assertTrue(request != null);
        try {
            assertEquals(HEADER, request.getHeaders());
        } catch (AuthFailureError e) {
            assertTrue(false);
        }
    }

    @Test
    public void testMakeJsonArrayRequest() {
        JsonArrayRequest request = factory.makeJsonArrayRequest(Request.Method.GET, HTTPService.DOMAIN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        assertTrue(request != null);
        try {
            assertEquals(HEADER, request.getHeaders());
        } catch (AuthFailureError e) {
            assertTrue(false);
        }
    }
}
