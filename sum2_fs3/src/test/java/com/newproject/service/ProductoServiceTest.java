package com.newproject.service;

import com.newproject.model.Producto;
import com.newproject.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.io.InputStream;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        Files.createDirectories(productoService.rutaDirectorio);
    }

    @Test
    void testObtenerImagen() throws Exception {
        Path path = productoService.rutaDirectorio.resolve("test.jpg");
        Files.createFile(path);

        Resource resource = productoService.obtenerImagen("test.jpg");

        assertTrue(resource.exists());
    }

    @Test
    void testObtenerTodosLosProductos() {
        Producto producto1 = new Producto();
        Producto producto2 = new Producto();
        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> result = productoService.obtenerTodosLosProductos();

        assertEquals(2, result.size());
    }

    @Test
    void testObtenerProductoPorId() {
        Producto producto = new Producto();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> result = productoService.obtenerProductoPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(producto, result.get());
    }

    @Test
    void testCrearProducto() {
        Producto producto = new Producto();
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto result = productoService.crearProducto(producto);

        assertEquals(producto, result);
    }

    @Test
    void testActualizarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto 1");
        Producto productoActualizado = new Producto();
        productoActualizado.setNombre("Producto Actualizado");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

        Producto result = productoService.actualizarProducto(1L, productoActualizado);

        assertEquals("Producto Actualizado", result.getNombre());
    }

    @Test
    void testEliminarProducto(){
        Producto producto = new Producto();
        producto.setUrl("test.jpg");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.eliminarProducto(1L);

        verify(productoRepository, times(1)).deleteById(1L);
        assertFalse(Files.exists(productoService.rutaDirectorio.resolve("test.jpg")));
    }

    @Test
    void testGuardarImagen() throws IOException {
        Path path = productoService.rutaDirectorio.resolve("test1.jpg");

        if (Files.exists(path)) {
            Files.delete(path);
        }
        when(multipartFile.getOriginalFilename()).thenReturn("test1.jpg");
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class)); // Mock InputStream

        String nombreArchivo = productoService.guardarImagen(multipartFile);

        assertEquals("test1.jpg", nombreArchivo);
        assertTrue(Files.exists(productoService.rutaDirectorio.resolve("test1.jpg")));
    }

    @Test
    void testCargarImagen() throws IOException {
        Path path = productoService.rutaDirectorio.resolve("test2.jpg");

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        Resource resource = productoService.cargarImagen("test2.jpg");

        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }
}