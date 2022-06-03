package com.live.eggplant.base.encrypt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.live.eggplant.base.encrypt.EncryUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson mGson;//gson对象
    private final TypeAdapter<T> adapter;

    /**
     * 构造器
     */
    public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.mGson = gson;
        this.adapter = adapter;
    }

    /**
     * 转换
     *
     * @param responseBody
     * @return
     * @throws IOException
     */
    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        try {
            String response = responseBody.string();
            JSONObject responseJsonObj = new JSONObject(response);
            if (responseJsonObj.has("d")) {
                String newResponse = responseJsonObj.getString("d");
                responseBody = ResponseBody.create(responseBody.contentType(),
                        EncryUtil.decryptAes(newResponse));
            } else {
                responseBody = ResponseBody.create(responseBody.contentType(), response);
            }
            JsonReader jsonReader = mGson.newJsonReader(responseBody.charStream());
            T result = adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            responseBody.close();
        }
        return null;
    }
}