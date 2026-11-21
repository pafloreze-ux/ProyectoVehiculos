package com.proyecto.vehiculos.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehiculo_documento")
public class VehiculoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "documento_id", nullable = false)
    private DocumentoParametrico documento;

    @Column(name = "fecha_expedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false, length = 20)
    private String estado; // 'Habilitado', 'Vencido', 'En Verificación'

    private String observaciones;

    @Lob
    @Column(name = "documento_pdf", columnDefinition = "LONGBLOB")
    private byte[] documento_pdf;

    public byte[] getDocumento_pdf() {
        return documento_pdf;
    }

    public void setDocumento_pdf(byte[] documento_pdf) {
        this.documento_pdf = documento_pdf;
    }
}