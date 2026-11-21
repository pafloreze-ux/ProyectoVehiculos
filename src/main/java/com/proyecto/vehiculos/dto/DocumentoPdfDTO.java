package com.proyecto.vehiculos.dto;

public class DocumentoPdfDTO {
    private Long idDocumento; // ID del registro vehiculo_documento
    private String pdfBase64;  // Cadena del archivo PDF codificado en Base64

    public DocumentoPdfDTO() {}

    public Long getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Long idDocumento) { this.idDocumento = idDocumento; }
    public String getPdfBase64() { return pdfBase64; }
    public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}