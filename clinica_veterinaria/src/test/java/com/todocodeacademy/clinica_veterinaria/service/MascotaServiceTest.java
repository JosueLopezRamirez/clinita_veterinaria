package com.todocodeacademy.clinica_veterinaria.service;

import com.todocodeacademy.clinica_veterinaria.dto.MascoDuenioDTO;
import com.todocodeacademy.clinica_veterinaria.model.Duenio;
import com.todocodeacademy.clinica_veterinaria.model.Mascota;
import com.todocodeacademy.clinica_veterinaria.repository.IMascotaRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private IMascotaRepository repoMasco;

    @InjectMocks
    private MascotaService service;

    private Mascota mascota(Long id, String nombre, String especie, String raza) {
        return new Mascota(id, nombre, especie, raza, "color");
    }

    private Duenio duenio(Long id, String nombre, String apellido) {
        return new Duenio(id, "dni", nombre, apellido, "celular");
    }

    @Test
    void getMascotas_devuelveLaListaDelRepositorio() {
        List<Mascota> esperado = List.of(
                mascota(1L, "Rex", "perro", "caniche"),
                mascota(2L, "Michi", "gato", "siames"));
        when(repoMasco.findAll()).thenReturn(esperado);

        List<Mascota> resultado = service.getMascotas();

        assertEquals(esperado, resultado);
        verify(repoMasco).findAll();
    }

    @Test
    void saveMascota_delegaEnElRepositorio() {
        Mascota mascota = mascota(1L, "Rex", "perro", "caniche");

        service.saveMascota(mascota);

        verify(repoMasco).save(mascota);
    }

    @Test
    void deleteMascota_delegaEnElRepositorio() {
        service.deleteMascota(5L);

        verify(repoMasco).deleteById(5L);
    }

    @Test
    void findMascota_devuelveLaMascotaCuandoExiste() {
        Mascota mascota = mascota(1L, "Rex", "perro", "caniche");
        when(repoMasco.findById(1L)).thenReturn(Optional.of(mascota));

        Mascota resultado = service.findMascota(1L);

        assertEquals(mascota, resultado);
    }

    @Test
    void findMascota_devuelveNullCuandoNoExiste() {
        when(repoMasco.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.findMascota(99L));
    }

    @Test
    void getCaniches_filtraSoloPerrosDeRazaCanicheIgnorandoMayusculas() {
        Mascota caniche = mascota(1L, "Rex", "PERRO", "CANICHE");
        Mascota perroOtroRaza = mascota(2L, "Bobby", "perro", "labrador");
        Mascota otroEspecie = mascota(3L, "Michi", "gato", "caniche");
        when(repoMasco.findAll()).thenReturn(List.of(caniche, perroOtroRaza, otroEspecie));

        List<Mascota> resultado = service.getCaniches();

        assertEquals(1, resultado.size());
        assertEquals("Rex", resultado.get(0).getNombre());
        assertTrue(resultado.contains(caniche));
        assertTrue(!resultado.contains(perroOtroRaza));
        assertTrue(!resultado.contains(otroEspecie));
    }

    @Test
    void getCaniches_devuelveListaVaciaCuandoNoHayCaniches() {
        Mascota perroOtroRaza = mascota(1L, "Bobby", "perro", "labrador");
        when(repoMasco.findAll()).thenReturn(List.of(perroOtroRaza));

        List<Mascota> resultado = service.getCaniches();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void getCaniches_devuelveListaVaciaCuandoElRepositorioNoTieneMascotas() {
        when(repoMasco.findAll()).thenReturn(List.of());

        List<Mascota> resultado = service.getCaniches();

        assertTrue(resultado.isEmpty());
        verify(repoMasco).findAll();
    }

    @Test
    void getMascoDuenios_mapeaTodasLasMascotasConSuDuenio() {
        Duenio duenio1 = duenio(1L, "Juan", "Perez");
        Duenio duenio2 = duenio(2L, "Ana", "Lopez");
        Mascota rex = mascota(1L, "Rex", "perro", "caniche");
        rex.setDuenio(duenio1);
        Mascota michi = mascota(2L, "Michi", "gato", "siames");
        michi.setDuenio(duenio2);
        when(repoMasco.findAll()).thenReturn(List.of(rex, michi));

        List<MascoDuenioDTO> resultado = service.getMascoDuenios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        MascoDuenioDTO primerDto = resultado.get(0);
        assertEquals("Rex", primerDto.getNombre_mascota());
        assertEquals("perro", primerDto.getEspecie());
        assertEquals("caniche", primerDto.getRaza());
        assertEquals("Juan", primerDto.getNombre_duenio());
        assertEquals("Perez", primerDto.getApellido_duenio());

        MascoDuenioDTO segundoDto = resultado.get(1);
        assertEquals("Michi", segundoDto.getNombre_mascota());
        assertEquals("gato", segundoDto.getEspecie());
        assertEquals("siames", segundoDto.getRaza());
        assertEquals("Ana", segundoDto.getNombre_duenio());
        assertEquals("Lopez", segundoDto.getApellido_duenio());

        assertEquals("Rex", primerDto.getNombre_mascota());
        assertEquals("Michi", segundoDto.getNombre_mascota());
        verify(repoMasco).findAll();
    }

    @Test
    void getMascoDuenios_devuelveListaVaciaSinMascotas() {
        when(repoMasco.findAll()).thenReturn(List.of());

        List<MascoDuenioDTO> resultado = service.getMascoDuenios();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void editMascota_guardaLosCambios() {
        Mascota mascota = mascota(1L, "Rex", "perro", "caniche");

        service.editMascota(mascota);

        verify(repoMasco).save(mascota);
        verify(repoMasco, never()).deleteById(any());
    }
}