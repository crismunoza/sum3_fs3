import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Producto } from '../../shared/models/Producto';
import { ProductoService } from '../../core/service/producto/producto.service';
import { user } from '../../shared/models/Usuario';
import { jwtDecode } from 'jwt-decode'; 
import { FormsModule } from '@angular/forms';
import { Ventas } from '../../shared/models/Ventas';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  productosNuevos: Producto[] = [];
  productosAntiguos: Producto[] = [];
  filtroPrecioMin: number = 0;
  filtroPrecioMax: number = 600000;
  filtroStock: boolean = false;
  compraRealizada: Ventas[] = [];

  constructor(private productoService: ProductoService) { }
  loggedInUser: user | null = null;

  ngOnInit(): void {
    this.obtenerProductos();
    this.checkLoginStatus();
  }

  obtenerProductos(): void {
    this.productoService.obtenerTodosLosProductos().subscribe(
      (productos) => {
        console.log('Productos obtenidos', productos);
        this.productosNuevos = productos.filter(producto => producto.nuevo === 1);
        this.productosAntiguos = productos.filter(producto => producto.nuevo === 0);
      },
      (error) => {
        console.error('Error al obtener productos', error);
      }
    );
  }

  aplicarFiltros(): void {
    this.productosNuevos = this.productosNuevos.filter(producto => 
      producto.precio >= this.filtroPrecioMin && 
      producto.precio <= this.filtroPrecioMax && 
      (!this.filtroStock || producto.stock > 0)
    );
    this.productosAntiguos = this.productosAntiguos.filter(producto => 
      producto.precio >= this.filtroPrecioMin && 
      producto.precio <= this.filtroPrecioMax && 
      (!this.filtroStock || producto.stock > 0)
    );
  }

  quitarFiltros(): void {
    this.filtroPrecioMin = 0;
    this.filtroPrecioMax = 600000;
    this.filtroStock = false;
    this.obtenerProductos();
  }

  checkLoginStatus() {
    if (typeof window !== 'undefined' && window.sessionStorage) {
      const token = sessionStorage.getItem('authToken');
  
      if (token) {
        try {
          // Decodificar el token
          const decodedToken: any = jwtDecode(token);
  
          // Asignar la información del usuario a loggedInUser
          this.loggedInUser = {
            username: decodedToken.sub,  
            role: decodedToken.role  
          };
          console.log(this.loggedInUser);
  
        } catch (error) {
          console.error('Error al decodificar el token', error);
        }
      } else {
        this.loggedInUser = null; 
      }
    }
  }
  comprarProducto(prod: Producto): void {
    if (this.loggedInUser) {
      // Crear un objeto de tipo Ventas que asocia al producto con el usuario
      const venta: Ventas = {
        producto: prod, // Aquí asignamos el producto a la venta
        usuario: this.loggedInUser.username, // Asociamos el usuario que realizó la compra
      };
  
      // Agregar la venta al carrito
      this.compraRealizada.push(venta);
  
      // Mostrar un mensaje rápido de confirmación
      Swal.fire({
        text: 'El producto ha sido añadido a tu carrito.',
        icon: 'success',
        timer: 1500, // Duración del mensaje en milisegundos
        showConfirmButton: false // Sin botón de confirmación
      });
  
      // Guardar el array completo en sessionStorage automáticamente
      sessionStorage.setItem('compraRealizada', JSON.stringify(this.compraRealizada));
    } else {
      console.log('Usuario no autenticado');
    }
  }
  
  
  
  
}
