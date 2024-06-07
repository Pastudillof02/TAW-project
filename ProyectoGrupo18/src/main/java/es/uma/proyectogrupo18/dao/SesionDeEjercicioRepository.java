package es.uma.proyectogrupo18.dao;

import es.uma.proyectogrupo18.entity.RutinaSemanalEntity;
import es.uma.proyectogrupo18.entity.SesionDeEjercicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SesionDeEjercicioRepository extends JpaRepository<SesionDeEjercicioEntity, Integer> {
    @Query("select s from SesionDeEjercicioEntity s where s.ejercicio = :ejercicio")
    public List<SesionDeEjercicioEntity> findSesionesByEjercicioId(@Param("ejercicio")int ejercicio);

    @Query("select s from SesionDeEjercicioEntity s where s.rutina = :rutina order by s.dia, s.orden asc")
    public List<SesionDeEjercicioEntity> findSesionesByRutina(@Param("rutina") RutinaSemanalEntity rutina);
}
