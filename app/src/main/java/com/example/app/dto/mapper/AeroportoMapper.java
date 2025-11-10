package com.example.app.dto.mapper;

import org.springframework.stereotype.Component;

import com.example.app.dto.request.AeroportoRequest;
import com.example.app.dto.response.AeroportoResponse;
import com.example.app.model.Aeroporto;

@Component
public class AeroportoMapper {

    public static Aeroporto toEntity( AeroportoRequest r ) {

        Aeroporto a = new Aeroporto();

        a.setNome( r.getNome() );
        a.setIata( r.getIata() );
        a.setCidade( r.getCidade() );
        a.setPais( r.getPais() );
        a.setLatitude( r.getLatitude() );
        a.setLongitude( r.getLongitude() );
        a.setAltitude( r.getAltitude() );

        return a;

    }

    public static AeroportoResponse fromEntity( Aeroporto a ) {

        AeroportoResponse r = new AeroportoResponse();

        r.setId( a.getId() );
        r.setNome( a.getNome() );
        r.setIata( a.getIata() );
        r.setCidade( a.getCidade() );
        r.setPais( a.getPais() );
        r.setLatitude( a.getLatitude() );
        r.setLongitude( a.getLongitude() );
        r.setAltitude( a.getAltitude() );

        return r;

    }

}