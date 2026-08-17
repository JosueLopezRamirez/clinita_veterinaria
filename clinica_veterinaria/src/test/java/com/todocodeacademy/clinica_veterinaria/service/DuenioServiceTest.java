package com.todocodeacademy.clinica_veterinaria.service;

import com.todocodeacademy.clinica_veterinaria.model.Duenio;
import com.todocodeacademy.clinica_veterinaria.repository.IDuenioRepository;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
class DuenioServiceTest {

    @Mock
    private IDuenioRepository repoDuenio;

    @InjectMocks
    private DuenioService service;

    private Duenio duenio(Long id, String nombre, String apellido) {
        return new Duenio(id, "dni", nombre, apellido, "celular");
    }

    @Test
    void getDuenios_devuelveLaListaDelRepositorio() {
        Duenio duenio1 = duenio(1L, "Juan", "Perez");
        Duenio duenio2 = duenio(2L, "Ana", "Lopez");
        when(repoDuenio.findAll()).thenReturn(List.of(duenio1, duenio2));

        List<Duenio> resultado = service.getDuenios();

        assertEquals(List.of(duenio1, duenio2), resultado);
        verify(repoDuenio).findAll();
    }

    @Test
    void saveDuenio_delegaEnElRepositorio() {
        Duenio duenio = duenio(1L, "Juan", "Perez");

        service.saveDuenio(duenio);

        verify(repoDuenio).save(duenio);
    }

    @Test
    void deleteDuenio_delegaEnElRepositorio() {
        service.deleteDuenio(5L);

        verify(repoDuenio).deleteById(5L);
    }

    @Test
    void findDuenio_devuelveElDuenioCuandoExiste() {
        Duenio duenio = duenio(1L, "Juan", "Perez");
        when(repoDuenio.findById(1L)).thenReturn(Optional.of(duenio));

        Duenio resultado = service.findDuenio(1L);

        assertEquals(duenio, resultado);
    }

    @Test
    void findDuenio_devuelveNullCuandoNoExiste() {
        when(repoDuenio.findById(99L)).thenReturn(Optional.empty());

        assertNull(service.findDuenio(99L));
    }

    @Test
    void editDuenio_llamaASaveDuenio() {
        Duenio duenio = duenio(1L, "Juan", "Gomez");

        service.editDuenio(duenio);

        verify(repoDuenio).save(duenio);
        verify(repoDuenio, never()).deleteById(any());
    }
}