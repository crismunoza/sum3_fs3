package com.newproject.controller;

import com.newproject.model.Producto;
import com.newproject.service.CustomUserDetailsService;
import com.newproject.service.ProductoService;
import com.newproject.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testObtenerTodosLosProductos() throws Exception {
        // Crear datos simulados
        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Producto 1");
        producto1.setDescripcion("Descripción del producto 1");
        producto1.setPrecio(100.0);
        producto1.setStock(10);
        producto1.setUrl("imagen1.jpg");
        producto1.setNuevo(1);

        List<Producto> productosMock = Arrays.asList(producto1);

        // Simular el servicio
        when(productoService.obtenerTodosLosProductos()).thenReturn(productosMock);

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.get("/api/productos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value("Producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].descripcion").value("Descripción del producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].precio").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url").value("http://localhost/api/productos/imagenes/imagen1.jpg"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testObtenerProductoPorId() throws Exception {
        // Crear datos simulados
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto 1");
        producto.setDescripcion("Descripción del producto 1");
        producto.setPrecio(100.0);
        producto.setStock(10);
        producto.setUrl("http://localhost/api/productos/imagenes/imagen1.jpg");
        producto.setNuevo(1);

        // Simular el servicio
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.get("/api/productos/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripción del producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.precio").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("http://localhost/api/productos/imagenes/imagen1.jpg"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testObtenerProductoPorIdNotFound() throws Exception {
        // Simular el servicio
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.empty());

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.get("/api/productos/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Producto no encontrado"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearProducto() throws Exception {
        // Crear datos simulados
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto 1");
        producto.setDescripcion("Descripción del producto 1");
        producto.setPrecio(100.0);
        producto.setStock(10);
        producto.setUrl("imagen1.jpg");
        producto.setNuevo(1);

        // Simular el servicio
        when(productoService.crearProducto(any(Producto.class))).thenReturn(producto);

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Producto 1\",\"descripcion\":\"Descripción del producto 1\",\"precio\":100.0,\"stock\":10,\"nuevo\":1,\"url\":\"imagen1.jpg\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripción del producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.precio").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("imagen1.jpg"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActualizarProducto() throws Exception {
        // Crear datos simulados
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Actualizado");
        producto.setDescripcion("Descripción Actualizada");
        producto.setPrecio(150.0);
        producto.setStock(20);
        producto.setUrl("imagen_actualizada.jpg");
        producto.setNuevo(0);

        // Simular el servicio
        when(productoService.actualizarProducto(anyLong(), any(Producto.class))).thenReturn(producto);

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Producto Actualizado\",\"descripcion\":\"Descripción Actualizada\",\"precio\":150.0,\"stock\":20,\"nuevo\":0,\"url\":\"imagen_actualizada.jpg\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Producto Actualizado"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripción Actualizada"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.precio").value(150.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("imagen_actualizada.jpg"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarProducto() throws Exception {
        // Simular el servicio
        doNothing().when(productoService).eliminarProducto(1L);

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/productos/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCrearProductoConImagen() throws Exception {
        // Crear datos simulados
        MockMultipartFile imagen = new MockMultipartFile("imagen", "imagen.jpg", MediaType.IMAGE_JPEG_VALUE, "imagen".getBytes());
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto 1");
        producto.setDescripcion("Descripción del producto 1");
        producto.setPrecio(100.0);
        producto.setStock(10);
        producto.setUrl("imagen.jpg");
        producto.setNuevo(1);

        // Simular el servicio
        when(productoService.guardarImagen(any(MultipartFile.class))).thenReturn("imagen.jpg");
        when(productoService.crearProducto(any(Producto.class))).thenReturn(producto);

        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/productos/upload")
                .file(imagen)
                .param("nombre", "Producto 1")
                .param("descripcion", "Descripción del producto 1")
                .param("precio", "100.0")
                .param("stock", "10")
                .param("nuevo", "1"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripción del producto 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.precio").value(100.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("imagen.jpg"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testObtenerImagen() throws Exception {
        // Crear datos simulados
        Path rutaArchivo = Paths.get("src/main/resources/static/images").resolve("imagen.jpg").normalize();
        Files.createDirectories(rutaArchivo.getParent());
        
        // Solo crear el archivo si no existe
        if (!Files.exists(rutaArchivo)) {
            Files.createFile(rutaArchivo);
        }
    
        Resource recurso = new ClassPathResource("static/images/imagen.jpg");
    
        // Simular el servicio
        when(productoService.obtenerImagen("imagen.jpg")).thenReturn(recurso);
    
        // Realizar la solicitud HTTP simulada
        mockMvc.perform(MockMvcRequestBuilders.get("/api/productos/imagenes/imagen.jpg"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, Files.probeContentType(rutaArchivo)))
                .andDo(MockMvcResultHandlers.print());
    
        // Eliminar el archivo después de la prueba
        Files.deleteIfExists(rutaArchivo);
    }
    
}