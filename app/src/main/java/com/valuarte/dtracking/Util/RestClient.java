package com.valuarte.dtracking.Util;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.RequiresHeader;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;

/**
 * Created by Jose Williams Garcia on 31/5/2017.
 */

@Rest(rootUrl = Utilidades.URL_HOST,
        converters = {FormHttpMessageConverter.class, StringHttpMessageConverter.class, GsonHttpMessageConverter.class})
public interface RestClient extends RestClientErrorHandling, RestClientHeaders {
    @Post("/dtracking/movil/tipos_gestion/")
    String tipos_gestion();
    @Post("/dtracking/movil/gestiones/")
    String gestiones(@Field String user);
    @Post("/dtracking/movil/seguimiento_gps/")
    String seguimiento_gps(@Field String user,
                           @Field String latitude,
                           @Field String longitude,
                           @Field String fecha);

    @Post("/dtracking/movil/mensajeria/")
    String mensajeria();
    @Post("/dtracking/movil/login/")
    String login(@Field String username,
                 @Field String password);
    @Post("/dtracking/movil/cargar_gestion/")
    String cargar_gestion(
            @Field String gestion,
            @Field String latitude,
            @Field String longitude,
            @Field String fecha,
            @Field String json,
            @Field String user);

    @Post("/dtracking/movil/cargar_media/")
    @RequiresHeader("Content-Type")
    String cargar_media(@Body MultiValueMap<String, Object> data);
}