// Función para colapsar/expandir el sidebar
function toggleSidebar() {
    const sidebar = document.querySelector('.col-md-2.bg-dark');
    const mainContent = document.querySelector('.col-md-10, .col-md-11');
    
    if (!sidebar) return;
    
    sidebar.classList.toggle('collapsed');
    
    // Cerrar todos los dropdowns si están abiertos al colapsar
    if (sidebar.classList.contains('collapsed')) {
        const dropdowns = document.querySelectorAll('.nav-dropdown');
        dropdowns.forEach(dropdown => {
            dropdown.classList.remove('active');
        });
        
        // Sidebar colapsado - expandir contenido principal
        if (mainContent) {
            mainContent.classList.remove('col-md-10');
            mainContent.classList.add('col-md-11');
        }
        // Guardar estado colapsado
        localStorage.setItem('sidebarCollapsed', 'true');
    } else {
        // Sidebar expandido - contenido principal normal
        if (mainContent) {
            mainContent.classList.remove('col-md-11');
            mainContent.classList.add('col-md-10');
        }
        // Guardar estado expandido
        localStorage.setItem('sidebarCollapsed', 'false');
    }
}

// Función para toggle del dropdown de operaciones e historial
function toggleDropdown(event) {
    event.preventDefault();
    
    const sidebar = document.querySelector('.col-md-2.bg-dark');
    
    // Si el sidebar está colapsado, no hacer toggle (se maneja con hover)
    if (sidebar && sidebar.classList.contains('collapsed')) {
        return;
    }
    
    const dropdown = event.currentTarget.closest('.nav-dropdown');
    
    if (dropdown) {
        const wasActive = dropdown.classList.contains('active');
        
        // Cerrar todos los dropdowns
        const allDropdowns = document.querySelectorAll('.nav-dropdown');
        allDropdowns.forEach(d => {
            d.classList.remove('active');
            // Guardar estado cerrado
            const pageName = d.querySelector('[data-page]').getAttribute('data-page');
            localStorage.setItem(`dropdown-${pageName}`, 'false');
        });
        
        // Si no estaba activo, activar este
        if (!wasActive) {
            dropdown.classList.add('active');
            // Guardar estado del dropdown
            const pageName = event.currentTarget.getAttribute('data-page');
            localStorage.setItem(`dropdown-${pageName}`, 'true');
        }
    }
}

// Función para marcar el link activo según la URL actual
function setActiveLink() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link-custom');
    
    navLinks.forEach(link => {
        // Remover clase active de todos los links
        link.classList.remove('active');
        
        // Obtener el identificador de la página del atributo data-page
        const pageName = link.getAttribute('data-page');
        
        // Si la URL actual contiene el nombre de la página, marcarla como activa
        if (pageName && currentPath.includes(pageName)) {
            link.classList.add('active');
            
            // Si es un submenú, activar también el dropdown padre
            if (link.classList.contains('nav-link-sub')) {
                const dropdown = link.closest('.nav-dropdown');
                if (dropdown) {
                    dropdown.classList.add('active');
                    // Marcar también el link padre como activo
                    const parentLink = dropdown.querySelector('.nav-link-custom:not(.nav-link-sub)');
                    if (parentLink) {
                        parentLink.classList.add('active');
                    }
                }
            }
        }
    });
}

// Función para aplicar el estado guardado del sidebar INMEDIATAMENTE
function applySidebarState() {
    const savedState = localStorage.getItem('sidebarCollapsed');
    const sidebarCol = document.querySelector('.col-md-2.bg-dark');
    const mainContent = document.querySelector('.col-md-10, .col-md-11');
    
    if (savedState === 'true' && sidebarCol) {
        // Aplicar estado colapsado ANTES de que se renderice
        sidebarCol.classList.add('sidebar', 'collapsed');
        if (mainContent) {
            mainContent.classList.remove('col-md-10');
            mainContent.classList.add('col-md-11');
        }
    } else if (sidebarCol) {
        sidebarCol.classList.add('sidebar');
    }
}

// Función para restaurar el estado de los dropdowns
function restoreDropdownState() {
    const sidebar = document.querySelector('.col-md-2.bg-dark');
    
    // Solo restaurar si el sidebar NO está colapsado
    if (!sidebar || sidebar.classList.contains('collapsed')) {
        return;
    }
    
    // Restaurar estado de cada dropdown
    const dropdowns = document.querySelectorAll('.nav-dropdown');
    dropdowns.forEach(dropdown => {
        const link = dropdown.querySelector('[data-page]');
        if (link) {
            const pageName = link.getAttribute('data-page');
            const savedState = localStorage.getItem(`dropdown-${pageName}`);
            
            if (savedState === 'true') {
                dropdown.classList.add('active');
            }
        }
    });
}

// Ejecutar INMEDIATAMENTE antes de DOMContentLoaded para evitar parpadeo
applySidebarState();

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    // Aplicar estado guardado nuevamente por si acaso
    applySidebarState();
    
    // Marcar el link activo según la URL
    setActiveLink();
    
    // Restaurar estado del dropdown
    restoreDropdownState();
    
    // Agregar efecto de hover mejorado (opcional)
    const navLinks = document.querySelectorAll('.nav-link-custom');
    navLinks.forEach(link => {
        link.addEventListener('mouseenter', function() {
            this.style.transition = 'all 0.2s ease';
        });
    });
    
    // Cerrar dropdown al hacer clic fuera de él (solo cuando no está colapsado)
    document.addEventListener('click', function(event) {
        const sidebar = document.querySelector('.col-md-2.bg-dark');
        
        // Solo aplicar si el sidebar NO está colapsado
        if (sidebar && !sidebar.classList.contains('collapsed')) {
            if (!event.target.closest('.nav-dropdown')) {
                const dropdowns = document.querySelectorAll('.nav-dropdown');
                dropdowns.forEach(dropdown => {
                    dropdown.classList.remove('active');
                    const link = dropdown.querySelector('[data-page]');
                    if (link) {
                        const pageName = link.getAttribute('data-page');
                        localStorage.setItem(`dropdown-${pageName}`, 'false');
                    }
                });
            }
        }
    });
    
    // Posicionar los menús laterales correctamente cuando el sidebar está colapsado
    const sidebar = document.querySelector('.col-md-2.bg-dark');
    const navDropdowns = document.querySelectorAll('.nav-dropdown');
    
    navDropdowns.forEach(navDropdown => {
        const dropdownSubmenu = navDropdown.querySelector('.dropdown-submenu');
        
        if (sidebar && dropdownSubmenu) {
            // Usar mouseenter en lugar de hover para mejor control
            navDropdown.addEventListener('mouseenter', function() {
                if (sidebar.classList.contains('collapsed')) {
                    const navLink = this.querySelector('.nav-link-custom:not(.nav-link-sub)');
                    if (navLink) {
                        const rect = navLink.getBoundingClientRect();
                        // Ajustar la posición para que quede alineado perfectamente
                        dropdownSubmenu.style.top = (rect.top - 8) + 'px';
                    }
                }
            });
            
            // Actualizar posición durante el scroll
            window.addEventListener('scroll', function() {
                if (sidebar.classList.contains('collapsed')) {
                    const navLink = navDropdown.querySelector('.nav-link-custom:not(.nav-link-sub)');
                    if (navLink) {
                        const rect = navLink.getBoundingClientRect();
                        dropdownSubmenu.style.top = (rect.top - 8) + 'px';
                    }
                }
            });
        }
    });
});