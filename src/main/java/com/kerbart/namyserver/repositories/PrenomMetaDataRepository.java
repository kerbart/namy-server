package com.kerbart.namyserver.repositories;

import com.kerbart.namyserver.model.PrenomMetaData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface PrenomMetaDataRepository extends CrudRepository<PrenomMetaData, Long> {

    List<PrenomMetaData> findAll();
}
