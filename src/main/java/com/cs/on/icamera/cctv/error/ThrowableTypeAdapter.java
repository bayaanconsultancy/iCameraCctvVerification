package com.cs.on.icamera.cctv.error;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ThrowableTypeAdapter extends TypeAdapter<Throwable> {
	@Override
	public void write(JsonWriter out, Throwable value) throws IOException {
		out.beginObject();
		if (value != null) {
			out.name("message");
			out.value(value.getMessage());
		}
		out.endObject();
	}

	@Override
	public Throwable read(JsonReader in) throws IOException {
		in.beginObject();
		String message = "Expected 'message' field in the JSON for Throwable.";
		while (in.hasNext()) {
			String name = in.nextName();
			if ("message".equals(name)) {
				message = in.nextString();
			} else {
				in.skipValue();
			}
		}
		in.endObject();
		return new Throwable(message);
	}
}