package com.ics.ics_hc_offline.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.common.base.Utf8;
import com.ics.ics_hc_offline.ViewPDFActivity;
import com.ics.ics_hc_offline.database.HojaConsultaDBAdapter;
import com.ics.ics_hc_offline.database.constants.MainDBConstants;
import com.ics.ics_hc_offline.dto.DiagnosticoDTO;
import com.ics.ics_hc_offline.dto.EscuelaPacienteDTO;
import com.ics.ics_hc_offline.dto.HojaConsultaOffLineDTO;
import com.ics.ics_hc_offline.dto.PacienteDTO;
import com.ics.ics_hc_offline.dto.UsuarioDTO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeCodabar;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TemplatePDF {

    private Context CONTEXT;
    private HojaConsultaDBAdapter mDbAdapter;
    private static PacienteDTO paciente = null;
    private File PDFFILE;
    private Document DOCUMENT;
    private PdfWriter PDFWRITER;
    private Paragraph PARAGRAPH;
    private static final Font f = new Font(Font.FontFamily.TIMES_ROMAN, 8f, Font.NORMAL, BaseColor.BLACK);
    public TemplatePDF(Context context) {
        this.CONTEXT = context;
    }

    public void openDocument() {
        createFile();
        try {
            DOCUMENT = new Document(PageSize.A4);
            DOCUMENT.setMargins(0,0,20,0);
            PDFWRITER = PdfWriter.getInstance(DOCUMENT, new FileOutputStream(PDFFILE));
            DOCUMENT.open();
        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile() {
        File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
        if (arch.exists()) {
            arch.delete();
        }
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF-HC-OFFLINE");
        if (!folder.exists()) {
            folder.mkdir();
        }
        PDFFILE = new File(folder, "hojaConsultaOffline.pdf");
    }

    public void closeDocument() {
        DOCUMENT.close();
    }

    public void viewPdf() {
        Intent intent = new Intent(CONTEXT, ViewPDFActivity.class);
        intent.putExtra("path", PDFFILE.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CONTEXT.startActivity(intent);
    }

    public void appViewPdf() {
        try {
            File arch = new File("/storage/emulated/0/PDF-HC-OFFLINE/hojaConsultaOffline.pdf");
            if (arch.exists()) {
                Uri uri = FileProvider.getUriForFile(CONTEXT, CONTEXT.getApplicationContext().getPackageName() + ".provider", arch);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                //intent.setAction(StorageManager.ACTION_CLEAR_APP_CACHE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //error si se quita esto
                try {
                    CONTEXT.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CONTEXT, "ERROR", Toast.LENGTH_LONG).show();
                }
            }
        } catch (ActivityNotFoundException e) {
            Log.e("appViewPdf", e.toString());
        }
    }

    public void generateHojaConsultaPdf(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            crearCabecera(hojaConsulta);
        } catch (Exception e) {
            Log.e("generateHojaConsultaPdf", e.toString());
        }
    }

    public void crearCabecera(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            //Font f = new Font(Font.FontFamily.TIMES_ROMAN, 7f, Font.NORMAL, BaseColor.BLACK);
            mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
            mDbAdapter.open();
            paciente = mDbAdapter.getParticipante(MainDBConstants.codExpediente  + "='" + hojaConsulta.getCodExpediente() + "'", null);
            PdfPTable table = new PdfPTable(1);
            //PARAGRAPH = new Paragraph();
            Paragraph paragraphEstudio = new Paragraph();
            paragraphEstudio.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 100));
            StringBuilder estudios = new StringBuilder();
            String separador = ", ";
            if (hojaConsulta.getEstudiosParticipantes() != null && !hojaConsulta.getEstudiosParticipantes().equals("")) {
                String[] separated = hojaConsulta.getEstudiosParticipantes().split(",");
                for (String s : separated) {
                    if (s.trim().equals("Dengue")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("DEN");
                        } else {
                            estudios.append(separador).append("DEN");
                        }
                    }
                    if (s.trim().equals("Influenza")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("FLU");
                        } else {
                            estudios.append(separador).append("FLU");
                        }
                    }
                    if (s.trim().equals("Cohorte BB")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("BB");
                        } else {
                            estudios.append(separador).append("BB");
                        }
                    }
                    if (s.trim().equals("Transmision")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("TR");
                        } else {
                            estudios.append(separador).append("TR");
                        }
                    }
                    if (s.trim().equals("Chikungunya")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("CHIK");
                        } else {
                            estudios.append(separador).append("CHIK");
                        }
                    }
                    if (s.trim().equals("Seroprevalencia Chik")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("S-CHIK");
                        } else {
                            estudios.append(separador).append("S-CHIK");
                        }
                    }
                    if (s.trim().equals("Transmision Chik")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("TR-CHIK");
                        } else {
                            estudios.append(separador).append("TR-CHIK");
                        }
                    }
                    if (s.trim().equals("Indice Cluster Dengue")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("IC-DEN");
                        } else {
                            estudios.append(separador).append("IC-DEN");
                        }
                    }
                    if (s.trim().equals("Transmision Zika")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("TR-ZIKA");
                        } else {
                            estudios.append(separador).append("TR-ZIKA");
                        }
                    }
                    if (s.trim().equals("CH Familia")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("CH-F");
                        } else {
                            estudios.append(separador).append("CH-F");
                        }
                    }
                    if (s.trim().equals("Arbovirus Seroprev.")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("A-SERO");
                        } else {
                            estudios.append(separador).append("A-SERO");
                        }
                    }
                    if (s.trim().equals("Influenza UO1")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("UO1");
                        } else {
                            estudios.append(separador).append("UO1");
                        }
                    }
                    if (s.trim().equals("UO1")) {
                        if (estudios.toString().equals("")) {
                            estudios = new StringBuilder("UO1");
                        } else {
                            estudios.append(separador).append("UO1");
                        }
                    }
                }
                table.addCell(getCell(estudios.toString(), Element.ALIGN_RIGHT));
            } else {
                table.addCell(getCell("", Element.ALIGN_RIGHT));
            }

            Paragraph paragraph = new Paragraph();
            PdfPTable table1 = new PdfPTable(5);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(PdfPCell.NO_BORDER);
            table1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.setWidths(new float[]{5f, 1f, 1f, 1f,1f});
            /*Cabecera*/
            cell.setPhrase(new Phrase("HOJA DE CONSULTA ATENCION INTEGRAL A LA NIÑEZ", f));
            table1.addCell(cell);
            cell.setPhrase(new Phrase("CODIGO", f));
            table1.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(hojaConsulta.getCodExpediente()), f));
            table1.addCell(cell);
            cell.setPhrase(new Phrase("FECHA", f));
            table1.addCell(cell);
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = null;
            date = originalFormat.parse(hojaConsulta.getFechaConsulta());
            String formattedDate = targetFormat.format(date);
            cell.setPhrase(new Phrase(formattedDate, f));
            table1.addCell(cell);
            table.completeRow();

            /**/
            Paragraph paragraph2 = new Paragraph();
            PdfPTable table2 = new PdfPTable(5);
            PdfPCell cell2 = new PdfPCell();
            cell2.setBorder(PdfPCell.NO_BORDER);
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.setWidths(new float[]{2f, 4f, 6f, 2f, 2f});
            if (hojaConsulta.getExpedienteFisico() != null && !hojaConsulta.getExpedienteFisico().equals("")) {
                cell2.setPhrase(new Phrase("EXP  " + hojaConsulta.getExpedienteFisico(), f));
                table2.addCell(cell2);
            } else {
                cell2.setPhrase(new Phrase("EXP  " + "----", f));
                table2.addCell(cell2);
            }

            cell2.setPhrase(new Phrase("NOMBRE Y APELLIDOS", f));
            table2.addCell(cell2);
            if (paciente != null)
                cell2.setPhrase(new Phrase(paciente.getNomPaciente(), f));
            table2.addCell(cell2);
            cell2.setPhrase(new Phrase("Hora", f));
            table2.addCell(cell2);
            String amPm = "";
            if (hojaConsulta.getHorasv() != null && !hojaConsulta.getHorasv().equals("")) {
                if (hojaConsulta.getHorasv().toLowerCase().contains("am")) {
                    amPm = "[am]/pm";
                } else {
                    amPm = "am/[pm]";
                }
            } else {
                amPm = "am/pm";
            }
            if (hojaConsulta.getHorasv() != null && !hojaConsulta.getHorasv().equals("")) {
                cell2.setPhrase(new Phrase(hojaConsulta.getHorasv().substring(0, 5) + "    " + amPm, f));
                table2.addCell(cell2);
                table.completeRow();
            } else {
                cell2.setPhrase(new Phrase("---" + "    " + amPm, f));
                table2.addCell(cell2);
                table.completeRow();
            }


            /*--*/
            Paragraph paragraph3 = new Paragraph();
            PdfPTable table3 = new PdfPTable(8);
            PdfPCell cell3 = new PdfPCell();
            cell3.setBorder(PdfPCell.NO_BORDER);
            table3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.setWidths(new float[]{2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f});
            if (hojaConsulta.getPesoKg() != null && hojaConsulta.getPesoKg() > 0) {
                cell3.setPhrase(new Phrase("Peso  " + hojaConsulta.getPesoKg() + "  Kg", f));
                table3.addCell(cell3);
            } else {
                cell3.setPhrase(new Phrase("Peso  " + "---" + "  Kg", f));
                table3.addCell(cell3);
            }
            if (hojaConsulta.getTallaCm() != null && hojaConsulta.getTallaCm() > 0) {
                cell3.setPhrase(new Phrase("Talla  " + hojaConsulta.getTallaCm() + "  Cm", f));
                table3.addCell(cell3);
            } else {
                cell3.setPhrase(new Phrase("Talla  " + "---" + "  Cm", f));
                table3.addCell(cell3);
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(paciente.getFechaNac());
            String edad = DateUtils.obtenerEdad(cal);
            cell3.setPhrase(new Phrase("Edad  " + edad, f));/**/
            table3.addCell(cell3);
            String sexo = "";
            if (paciente.getSexo().trim().equals("M")) {
                sexo = "[M]  F";
            } else {
                sexo = "M  [F]";
            }
            cell3.setPhrase(new Phrase("Sexo  " + sexo, f));/**/
            table3.addCell(cell3);
            cell3.setPhrase(new Phrase("P/A  " + hojaConsulta.getPas() + "/" + hojaConsulta.getPad(), f));
            table3.addCell(cell3);
            cell3.setPhrase(new Phrase("Fcia Resp  " + hojaConsulta.getFciaResp(), f));
            table3.addCell(cell3);
            cell3.setPhrase(new Phrase("Fcia Card  " + hojaConsulta.getFciaCard(), f));
            table3.addCell(cell3);
            cell3.setPhrase(new Phrase("Temp  " + hojaConsulta.getTemperaturac() + " ºC", f));
            table3.addCell(cell3);

            /*---*/
            Paragraph paragraph4 = new Paragraph();
            PdfPTable table4 = new PdfPTable(2);
            PdfPCell cell4 = new PdfPCell();
            cell4.setBorder(PdfPCell.NO_BORDER);
            table4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table4.setWidths(new float[]{4f, 7f});
            String lugarAtencion = "";
            if (hojaConsulta.getLugarAtencion() != null && !hojaConsulta.getLugarAtencion().equals("")) {
                if (hojaConsulta.getLugarAtencion().trim().equalsIgnoreCase("cs sfv")) {
                    lugarAtencion = "CS SFV [X]           Terreno [ ]";
                } else {
                    lugarAtencion = "CS SFV [ ]           Terreno [X]";
                }
            } else {
                lugarAtencion = "CS SFV [ ]           Terreno [ ]";
            }
            cell4.setPhrase(new Phrase("Lugar de Atencion:     " + lugarAtencion, f));
            table4.addCell(cell4);
            String consulta = "";
            if (hojaConsulta.getConsulta() != null && !hojaConsulta.getConsulta().equals("")) {
                if (hojaConsulta.getConsulta().trim().equals("Inicial")) {
                    consulta = "[X] Inicial           [ ] Seguimiento           [ ] Convaleciente";
                } else if (hojaConsulta.getConsulta().trim().equals("Seguimiento")) {
                    consulta = "[ ] Inicial           [X] Seguimiento           [ ] Convaleciente";
                } else {
                    consulta = "[ ] Inicial           [ ] Seguimiento           [X] Convaleciente";
                }
            } else {
                consulta = "[ ] Inicial           [ ] Seguimiento           [ ] Convaleciente";
            }
            cell4.setPhrase(new Phrase("Consulta:     " + consulta, f));
            table4.addCell(cell4);

            /*---*/
            Paragraph paragraph5 = new Paragraph();
            PdfPTable table5 = new PdfPTable(4);
            PdfPCell cell5 = new PdfPCell();
            cell5.setBorder(PdfPCell.NO_BORDER);
            table5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table5.setWidths(new float[]{6f, 8f, 6f, 4f});
            cell5.setPhrase(new Phrase("Seguimiento CHIK " + "  1   2   3   4   5", f));
            table5.addCell(cell5);
            String turno = "";
            if (hojaConsulta.getTurno() != null && !hojaConsulta.getTurno().equals("")) {
                if (hojaConsulta.getTurno().equals("1")) {
                    turno = "[X] Reg.     [ ] Noche     [ ] Fin Semana";
                } else if (hojaConsulta.getTurno().equals("2")) {
                    turno = "[ ] Reg.     [X] Noche     [ ] Fin Semana";
                } else {
                    turno = "[ ] Reg.     [ ] Noche     [X] Fin Semana";
                }
            } else {
                turno = "[ ] Reg.     [ ] Noche     [ ] Fin Semana";
            }
            cell5.setPhrase(new Phrase("Turno  " + turno, f));
            table5.addCell(cell5);
            if (hojaConsulta.getHora() != null && !hojaConsulta.getHora().equals("")) {
                cell5.setPhrase(new Phrase("Hora consulta  " + hojaConsulta.getHora(), f));
                table5.addCell(cell5);
            } else {
                cell5.setPhrase(new Phrase("Hora consulta  " + "", f));
                table5.addCell(cell5);
            }
            if (hojaConsulta.getTemMedc() != null) {
                cell5.setPhrase(new Phrase("T. Med.  " + hojaConsulta.getTemMedc() + " ºC", f));
                table5.addCell(cell5);
            } else {
                cell5.setPhrase(new Phrase("T. Med.  " + "" + " ºC", f));
                table5.addCell(cell5);
            }

            /*---*/
            Paragraph paragraph6 = new Paragraph();
            PdfPTable table6 = new PdfPTable(4);
            PdfPCell cell6 = new PdfPCell();
            cell6.setBorder(PdfPCell.NO_BORDER);
            table6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table6.setWidths(new float[]{5f, 5f, 12f, 20f});
            if (hojaConsulta.getFis() != null && !hojaConsulta.getFis().equals("")) {
                DateFormat originalFormatFis = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormatFis = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date fis = null;
                fis = originalFormatFis.parse(hojaConsulta.getFis());
                String formattedDatefis = targetFormatFis.format(fis);
                cell6.setPhrase(new Phrase("FIS  " + formattedDatefis, f));
                table6.addCell(cell6);
            } else {
                cell6.setPhrase(new Phrase("FIS  " + "-----", f));
                table6.addCell(cell6);
            }
            if (hojaConsulta.getFif() != null && !hojaConsulta.getFif().equals("")) {
                DateFormat originalFormatFif = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormatFif = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date fif = null;
                fif = originalFormatFif.parse(hojaConsulta.getFif());
                String formattedDatefif = targetFormatFif.format(fif);
                cell6.setPhrase(new Phrase("FIF  " + formattedDatefif, f));
                table6.addCell(cell6);
            } else {
                cell6.setPhrase(new Phrase("FIF  " + "-----", f));
                table6.addCell(cell6);
            }
            String amPmUltimoDiaFiebre = "";
            if (hojaConsulta.getUltDiaFiebre() != null && !hojaConsulta.getUltDiaFiebre().equals("")) {
                DateFormat originalFormatUltDiaFiebre = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormatUltDiaFiebre = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date ultDiaFiebre = null;
                ultDiaFiebre = originalFormatUltDiaFiebre.parse(hojaConsulta.getUltDiaFiebre());
                String formattedDateultDiaFiebre = targetFormatUltDiaFiebre.format(ultDiaFiebre);
                if (hojaConsulta.getAmPmUltDiaFiebre() != null && !hojaConsulta.getUltDiaFiebre().equals("")) {
                    if (hojaConsulta.getAmPmUltDiaFiebre().trim().equalsIgnoreCase("am")) {
                        amPmUltimoDiaFiebre = "[am]/pm";
                    } else {
                        amPmUltimoDiaFiebre = "am/[pm]";
                    }
                } else {
                    amPmUltimoDiaFiebre = "am/pm";
                }
                cell6.setPhrase(new Phrase("Último Día fiebre  " + formattedDateultDiaFiebre + " " + amPmUltimoDiaFiebre, f));
                table6.addCell(cell6);
            } else {
                cell6.setPhrase(new Phrase("Último Día fiebre  " + " -----   " + "am/pm", f));
                table6.addCell(cell6);
            }

            String amPmUltDosisAntipiretico = "";
            String horaUltDosisAntipiretico = "";
            if (hojaConsulta.getUltDosisAntipiretico() != null && !hojaConsulta.getUltDosisAntipiretico().equals("")) {
                DateFormat originalFormatUltDosisAntipiretico = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormatUltDosisAntipiretico = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date ultDosisAntipiretico = null;
                ultDosisAntipiretico = originalFormatUltDosisAntipiretico.parse(hojaConsulta.getUltDosisAntipiretico());
                String formattedDateultDosisAntipiretico = targetFormatUltDosisAntipiretico.format(ultDosisAntipiretico);
                if (hojaConsulta.getAmPmUltDosisAntipiretico() != null && !hojaConsulta.getAmPmUltDosisAntipiretico().equals("")) {
                    if (hojaConsulta.getAmPmUltDosisAntipiretico().trim().equalsIgnoreCase("a. m.")) {
                        amPmUltDosisAntipiretico = "[am]/pm";
                    } else {
                        amPmUltDosisAntipiretico = "am/[pm]";
                    }
                } else {
                    amPmUltDosisAntipiretico = "am/pm";
                }
                if (hojaConsulta.getHoraUltDosisAntipiretico() != null && !hojaConsulta.getHoraUltDosisAntipiretico().equals("")) {
                    horaUltDosisAntipiretico  = hojaConsulta.getHoraUltDosisAntipiretico().substring(0, hojaConsulta.getHoraUltDosisAntipiretico().length() -3);
                } else {
                    horaUltDosisAntipiretico = " ---- ";
                }
                cell6.setPhrase(new Phrase("Última Dosis Antipirético  " + formattedDateultDosisAntipiretico + "  Hora  " + horaUltDosisAntipiretico + "   " + amPmUltDosisAntipiretico, f));
                table6.addCell(cell6);
            } else {
                cell6.setPhrase(new Phrase("Última Dosis Antipirético  " + " ----- " + "Hora" + " --- " + "am/pm", f));
                table6.addCell(cell6);
            }
            paragraphEstudio.add(table);
            paragraph.add(table1);
            paragraph2.add(table2);
            paragraph3.add(table3);
            paragraph4.add(table4);
            paragraph5.add(table5);
            paragraph6.add(table6);
            DOCUMENT.add(paragraphEstudio);
            DOCUMENT.add(paragraph);
            DOCUMENT.add(paragraph2);
            DOCUMENT.add(paragraph3);
            DOCUMENT.add(paragraph4);
            DOCUMENT.add(paragraph5);
            DOCUMENT.add(paragraph6);
            generarFistPage(hojaConsulta);
        } catch (Exception e) {
            Log.e("crearCabecera", e.toString());
        }
    }

    public void generarFistPage(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            PdfPTable table = new PdfPTable(14);
            Paragraph paragraph = new Paragraph();
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(5);
            table.setWidths(new float[]{2.2f, 0.2f, 0.2f, 0.2f, 0.2f, 2.3f, 0.2f, 0.2f, 0.2f, 0.2f, 2.2f, 0.2f, 0.2f, 0.2f});
            //table.addCell(new PdfPCell(new Phrase("Estado General", f)));
            table.addCell(getCell2("Estado General", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("S", f)));
            table.addCell(new PdfPCell(new Phrase("N", f)));
            table.addCell(new PdfPCell(new Phrase("D", f)));
            //table.addCell(new PdfPCell(new Phrase("--", f)));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Gastrointestinal", f)));
            table.addCell(getCell2("Gastrointestinal", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("S", f)));
            table.addCell(new PdfPCell(new Phrase("N", f)));
            table.addCell(new PdfPCell(new Phrase("D", f)));
            //table.addCell(new PdfPCell(new Phrase("--", f)));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Osteomuscular", f)));
            table.addCell(getCell2("Osteomuscular", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("S", f)));
            table.addCell(new PdfPCell(new Phrase("N", f)));
            table.addCell(new PdfPCell(new Phrase("D", f)));
            table.addCell(new PdfPCell(new Phrase("Fiebre", f)));
            if (hojaConsulta.getFiebre() != null && !hojaConsulta.getFiebre().equals("")) {
                if (hojaConsulta.getFiebre().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getFiebre().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Poco apetito", f)));
            if (hojaConsulta.getPocoApetito() != null && !hojaConsulta.getPocoApetito().equals("")) {
                if (hojaConsulta.getPocoApetito().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getPocoApetito().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Artralgia", f)));
            if (hojaConsulta.getAltralgia() != null && !hojaConsulta.getAltralgia().equals("")) {
                if (hojaConsulta.getAltralgia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getAltralgia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Astenia", f)));
            if (hojaConsulta.getAstenia() != null && !hojaConsulta.getAstenia().equals("")) {
                if (hojaConsulta.getAstenia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getAstenia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Náusea", f)));
            if (hojaConsulta.getNausea() != null && !hojaConsulta.getNausea().equals("")) {
                if (hojaConsulta.getNausea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getNausea().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Mialgia", f)));
            if (hojaConsulta.getMialgia() != null && !hojaConsulta.getMialgia().equals("")) {
                if (hojaConsulta.getMialgia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getMialgia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Anormalmente Somnoliento", f)));
            if (hojaConsulta.getAsomnoliento() != null && !hojaConsulta.getAsomnoliento().equals("")) {
                if (hojaConsulta.getAsomnoliento().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Dificultad para Alimentarse", f)));
            if (hojaConsulta.getDificultadAlimentarse() != null && !hojaConsulta.getDificultadAlimentarse().equals("")) {
                if (hojaConsulta.getDificultadAlimentarse().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDificultadAlimentarse().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Lumbalgia", f)));
            if (hojaConsulta.getLumbalgia() != null && !hojaConsulta.getLumbalgia().equals("")) {
                if (hojaConsulta.getLumbalgia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getLumbalgia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Mal estado general", f)));
            if (hojaConsulta.getMalEstado() != null && !hojaConsulta.getMalEstado().equals("")) {
                if (hojaConsulta.getMalEstado().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Vómito # últimas 12 horas" + "  "  +(hojaConsulta.getVomito12h() > 0 ? hojaConsulta.getVomito12h() : "----")  , f)));
            if (hojaConsulta.getVomito12horas() != null && !hojaConsulta.getVomito12horas().equals("")) {
                if (hojaConsulta.getVomito12horas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getVomito12horas().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Dolor de cuello", f)));
            if (hojaConsulta.getDolorCuello() != null && !hojaConsulta.getDolorCuello().equals("")) {
                if (hojaConsulta.getDolorCuello().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDolorCuello().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Pérdida de conciencia", f)));
            if (hojaConsulta.getPerdidaConsciencia() != null && !hojaConsulta.getPerdidaConsciencia().equals("")) {
                if (hojaConsulta.getPerdidaConsciencia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Diarrea", f)));
            if (hojaConsulta.getDiarrea() != null && !hojaConsulta.getDiarrea().equals("")) {
                if (hojaConsulta.getDiarrea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDiarrea().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Tenosinovitis", f)));
            if (hojaConsulta.getTenosinovitis() != null && !hojaConsulta.getTenosinovitis().equals("")) {
                if (hojaConsulta.getTenosinovitis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getTenosinovitis().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Inquieto irritable", f)));
            if (hojaConsulta.getInquieto() != null && !hojaConsulta.getInquieto().equals("")) {
                if (hojaConsulta.getInquieto().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Diarrea con sangre", f)));
            if (hojaConsulta.getDiarreaSangre() != null && !hojaConsulta.getDiarreaSangre().equals("")) {
                if (hojaConsulta.getDiarreaSangre().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDiarreaSangre().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Artralgia proximal", f)));
            if (hojaConsulta.getArtralgiaProximal() != null && !hojaConsulta.getArtralgiaProximal().equals("")) {
                if (hojaConsulta.getArtralgiaProximal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getArtralgiaProximal().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Convulsiones", f)));
            if (hojaConsulta.getConvulsiones() != null && !hojaConsulta.getConvulsiones().equals("")) {
                if (hojaConsulta.getConvulsiones().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Estreñimiento", f)));
            if (hojaConsulta.getEstrenimiento() != null && !hojaConsulta.getEstrenimiento().equals("")) {
                if (hojaConsulta.getEstrenimiento().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEstrenimiento().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Artralgia distal", f)));
            if (hojaConsulta.getArtralgiaDistal() != null && !hojaConsulta.getArtralgiaDistal().equals("")) {
                if (hojaConsulta.getArtralgiaDistal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getArtralgiaDistal().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Hipotermia", f)));
            if (hojaConsulta.getHipotermia() != null && !hojaConsulta.getHipotermia().equals("")) {
                if (hojaConsulta.getHipotermia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Dolor abdominal intermitente", f)));
            if (hojaConsulta.getDolorAbIntermitente() != null && !hojaConsulta.getDolorAbIntermitente().equals("")) {
                if (hojaConsulta.getDolorAbIntermitente().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDolorAbIntermitente().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Conjuntivitis", f)));
            if (hojaConsulta.getConjuntivitis() != null && !hojaConsulta.getConjuntivitis().equals("")) {
                if (hojaConsulta.getConjuntivitis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getConjuntivitis().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Letargia", f)));
            if (hojaConsulta.getLetargia() != null && !hojaConsulta.getLetargia().equals("")) {
                if (hojaConsulta.getLetargia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Dolor abdominal continuo", f)));
            if (hojaConsulta.getDolorAbContinuo() != null && !hojaConsulta.getDolorAbContinuo().equals("")) {
                if (hojaConsulta.getDolorAbContinuo().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDolorAbContinuo().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Edema Articular en Muñecas", f)));
            if (hojaConsulta.getEdemaMunecas() != null && !hojaConsulta.getEdemaMunecas().equals("")) {
                if (hojaConsulta.getEdemaMunecas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEdemaMunecas().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            //table.addCell(new PdfPCell(new Phrase("Cabeza", f)));
            table.addCell(getCell2("Cabeza", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Epigastralgia", f)));
            if (hojaConsulta.getEpigastralgia() != null && !hojaConsulta.getEpigastralgia().equals("")) {
                if (hojaConsulta.getEpigastralgia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEpigastralgia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Edema Articular en Codos", f)));
            if (hojaConsulta.getEdemaCodos() != null && !hojaConsulta.getEdemaCodos().equals("")) {
                if (hojaConsulta.getEdemaCodos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEdemaCodos().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Cefalea", f)));
            if (hojaConsulta.getCefalea() != null && !hojaConsulta.getCefalea().equals("")) {
                if (hojaConsulta.getCefalea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getCefalea().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Intolerancia a la vía oral", f)));
            if (hojaConsulta.getIntoleranciaOral() != null && !hojaConsulta.getIntoleranciaOral().equals("")) {
                if (hojaConsulta.getIntoleranciaOral().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getIntoleranciaOral().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Edema Articular en Hombros", f)));
            if (hojaConsulta.getEdemaHombros() != null && !hojaConsulta.getEdemaHombros().equals("")) {
                if (hojaConsulta.getEdemaHombros().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEdemaHombros().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Rigidez de cuello", f)));
            if (hojaConsulta.getRigidezCuello() != null && !hojaConsulta.getRigidezCuello().equals("")) {
                if (hojaConsulta.getRigidezCuello().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Distensión abdominal ", f)));
            if (hojaConsulta.getDistensionAbdominal() != null && !hojaConsulta.getDistensionAbdominal().equals("")) {
                if (hojaConsulta.getDistensionAbdominal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDistensionAbdominal().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Edema Articular en Rodillas", f)));
            if (hojaConsulta.getEdemaRodillas() != null && !hojaConsulta.getEdemaRodillas().equals("")) {
                if (hojaConsulta.getEdemaRodillas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEdemaRodillas().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Inyección Conjuntival", f)));
            if (hojaConsulta.getInyeccionConjuntival() != null && !hojaConsulta.getInyeccionConjuntival().equals("")) {
                if (hojaConsulta.getInyeccionConjuntival().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Hepatomegalia " + (hojaConsulta.getHepatomegaliaCm() > 0 ? hojaConsulta.getHepatomegaliaCm() : "----")  + " cm", f)));
            if (hojaConsulta.getHepatomegalia() != null && !hojaConsulta.getHepatomegalia().equals("")) {
                if (hojaConsulta.getHepatomegalia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Edema Articular en Tobillos", f)));
            if (hojaConsulta.getEdemaTobillos() != null && !hojaConsulta.getEdemaTobillos().equals("")) {
                if (hojaConsulta.getEdemaTobillos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEdemaTobillos().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Hemorragia Subconjuntival", f)));
            if (hojaConsulta.getHemorragiaSuconjuntival() != null && !hojaConsulta.getHemorragiaSuconjuntival().equals("")) {
                if (hojaConsulta.getHemorragiaSuconjuntival().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Deshidratación", f)));
            table.addCell(getCell2("Deshidratación", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Cutáneo", f)));
            table.addCell(getCell2("Cutáneo", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Dolor Retrocular", f)));
            if (hojaConsulta.getDolorRetroocular() != null && !hojaConsulta.getDolorRetroocular().equals("")) {
                if (hojaConsulta.getDolorRetroocular().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDolorRetroocular().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Lengua y Mucosa Secas", f)));
            if (hojaConsulta.getLenguaMucosasSecas() != null && !hojaConsulta.getLenguaMucosasSecas().equals("")) {
                if (hojaConsulta.getLenguaMucosasSecas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rash Localizado", f)));
            if (hojaConsulta.getRahsLocalizado() != null && !hojaConsulta.getRahsLocalizado().equals("")) {
                if (hojaConsulta.getRahsLocalizado().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            //table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Fontanela Abombada", f)));
            if (hojaConsulta.getFontanelaAbombada() != null && !hojaConsulta.getFontanelaAbombada().equals("")) {
                if (hojaConsulta.getFontanelaAbombada().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Pliegue cútaneo", f)));
            if (hojaConsulta.getPliegueCutaneo() != null && !hojaConsulta.getPliegueCutaneo().equals("")) {
                if (hojaConsulta.getPliegueCutaneo().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rash generalizado", f)));
            if (hojaConsulta.getRahsGeneralizado() != null && !hojaConsulta.getRahsGeneralizado().equals("")) {
                if (hojaConsulta.getRahsGeneralizado().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            //table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Ictericia Conjuntival", f)));
            if (hojaConsulta.getIctericiaConuntival() != null && !hojaConsulta.getIctericiaConuntival().equals("")) {
                if (hojaConsulta.getIctericiaConuntival().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Orina reducida", f)));
            if (hojaConsulta.getOrinaReducida() != null && !hojaConsulta.getOrinaReducida().equals("")) {
                if (hojaConsulta.getOrinaReducida().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getOrinaReducida().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rash eritematoso", f)));
            if (hojaConsulta.getRashEritematoso() != null && !hojaConsulta.getRashEritematoso().equals("")) {
                if (hojaConsulta.getRashEritematoso().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            //table.addCell(new PdfPCell(new Phrase("Garganta", f)));
            table.addCell(getCell2("Garganta", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Bebe ávido, con Sed", f)));
            if (hojaConsulta.getBebeConSed() != null && !hojaConsulta.getBebeConSed().equals("")) {
                if (hojaConsulta.getBebeConSed().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rash macular", f)));
            if (hojaConsulta.getRahsMacular() != null && !hojaConsulta.getRahsMacular().equals("")) {
                if (hojaConsulta.getRahsMacular().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Eritema", f)));
            if (hojaConsulta.getEritema() != null && !hojaConsulta.getEritema().equals("")) {
                if (hojaConsulta.getEritema().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Ojos Hundidos", f)));
            if (hojaConsulta.getOjosHundidos() != null && !hojaConsulta.getOjosHundidos().equals("")) {
                if (hojaConsulta.getOjosHundidos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rash papular", f)));
            if (hojaConsulta.getRashPapular() != null && !hojaConsulta.getRashPapular().equals("")) {
                if (hojaConsulta.getRashPapular().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Dolor Garganta", f)));
            if (hojaConsulta.getDolorGarganta() != null && !hojaConsulta.getDolorGarganta().equals("")) {
                if (hojaConsulta.getDolorGarganta().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getDolorGarganta().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Fontanela Hundida", f)));
            if (hojaConsulta.getFontanelaHundida() != null && !hojaConsulta.getFontanelaHundida().equals("")) {
                if (hojaConsulta.getFontanelaHundida().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Piel moteada", f)));
            if (hojaConsulta.getRahsMoteada() != null && !hojaConsulta.getRahsMoteada().equals("")) {
                if (hojaConsulta.getRahsMoteada().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Adenopatías Cervicales", f)));
            if (hojaConsulta.getAdenopatiasCervicales() != null && !hojaConsulta.getAdenopatiasCervicales().equals("")) {
                if (hojaConsulta.getAdenopatiasCervicales().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Renal", f)));
            table.addCell(getCell2("Renal", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rubor Facial", f)));
            if (hojaConsulta.getRuborFacial() != null && !hojaConsulta.getRuborFacial().equals("")) {
                if (hojaConsulta.getRuborFacial().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Exudado", f)));
            if (hojaConsulta.getExudado() != null && !hojaConsulta.getExudado().equals("")) {
                if (hojaConsulta.getExudado().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Sintomas urinarios", f)));
            if (hojaConsulta.getRuborFacial() != null && !hojaConsulta.getRuborFacial().equals("")) {
                if (hojaConsulta.getRuborFacial().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getRuborFacial().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Equimosis", f)));
            if (hojaConsulta.getEquimosis() != null && !hojaConsulta.getEquimosis().equals("")) {
                if (hojaConsulta.getEquimosis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Petequias en mucosa", f)));
            if (hojaConsulta.getPetequiasMucosa() != null && !hojaConsulta.getPetequiasMucosa().equals("")) {
                if (hojaConsulta.getPetequiasMucosa().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Leucocituria >= 10 x Campo", f)));
            if (hojaConsulta.getLeucocituria() != null && !hojaConsulta.getLeucocituria().equals("")) {
                if (hojaConsulta.getLeucocituria().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getLeucocituria().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Cianosis Central", f)));
            if (hojaConsulta.getCianosisCentral() != null && !hojaConsulta.getCianosisCentral().equals("")) {
                if (hojaConsulta.getCianosisCentral().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            //table.addCell(new PdfPCell(new Phrase("Respiratorio", f)));
            table.addCell(getCell2("Respiratorio", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Nitritos", f)));
            if (hojaConsulta.getNitritos() != null && !hojaConsulta.getNitritos().equals("")) {
                if (hojaConsulta.getNitritos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getNitritos().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Ictericia", f)));
            if (hojaConsulta.getIctericia() != null && !hojaConsulta.getIctericia().equals("")) {
                if (hojaConsulta.getIctericia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Tos", f)));
            if (hojaConsulta.getTos() != null && !hojaConsulta.getTos().equals("")) {
                if (hojaConsulta.getTos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getTos().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Eritrocitos >= 6 x Campo", f)));
            if (hojaConsulta.getEritrocitos() != null && !hojaConsulta.getEritrocitos().equals("")) {
                if (hojaConsulta.getEritrocitos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getEritrocitos().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("Estado Nutricional", f)));
            table.addCell(getCell2("Estado Nutricional", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Rinorrea", f)));
            if (hojaConsulta.getRinorrea() != null && !hojaConsulta.getRinorrea().equals("")) {
                if (hojaConsulta.getRinorrea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getRinorrea().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Bilirrubinuria", f)));
            if (hojaConsulta.getBilirrubinuria() != null && !hojaConsulta.getBilirrubinuria().equals("")) {
                if (hojaConsulta.getBilirrubinuria().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getBilirrubinuria().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Obeso", f)));
            if (hojaConsulta.getObeso() != null && !hojaConsulta.getObeso().equals("")) {
                if (hojaConsulta.getObeso().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getObeso().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Congestión Nasal", f)));
            if (hojaConsulta.getCongestionNasal() != null && !hojaConsulta.getCongestionNasal().equals("")) {
                if (hojaConsulta.getCongestionNasal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getCongestionNasal().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Sobrepeso", f)));
            if (hojaConsulta.getSobrepeso() != null && !hojaConsulta.getSobrepeso().equals("")) {
                if (hojaConsulta.getSobrepeso().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getSobrepeso().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Otalgia", f)));
            if (hojaConsulta.getOtalgia() != null && !hojaConsulta.getOtalgia().equals("")) {
                if (hojaConsulta.getOtalgia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getOtalgia().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Interconsulta Pediatra", f)));
            if (hojaConsulta.getInterconsultaPediatrica() != null && !hojaConsulta.getInterconsultaPediatrica().equals("")) {
                if (hojaConsulta.getInterconsultaPediatrica().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Sospecha de Problema", f)));
            if (hojaConsulta.getSospechaProblema() != null && !hojaConsulta.getSospechaProblema().equals("")) {
                if (hojaConsulta.getSospechaProblema().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getSospechaProblema().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Aleteo Nasal", f)));
            if (hojaConsulta.getAleteoNasal() != null && !hojaConsulta.getAleteoNasal().equals("")) {
                if (hojaConsulta.getAleteoNasal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Referencia a Hospital", f)));
            if (hojaConsulta.getReferenciaHospital() != null && !hojaConsulta.getReferenciaHospital().equals("")) {
                if (hojaConsulta.getReferenciaHospital().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Normal", f)));
            if (hojaConsulta.getNormal() != null && !hojaConsulta.getNormal().equals("")) {
                if (hojaConsulta.getNormal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getNormal().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Apnea", f)));
            if (hojaConsulta.getApnea() != null && !hojaConsulta.getApnea().equals("")) {
                if (hojaConsulta.getApnea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Referencia por Dengue", f)));
            if (hojaConsulta.getReferenciaDengue() != null && !hojaConsulta.getReferenciaDengue().equals("")) {
                if (hojaConsulta.getReferenciaDengue().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Bajo Peso", f)));
            if (hojaConsulta.getBajoPeso() != null && !hojaConsulta.getBajoPeso().equals("")) {
                if (hojaConsulta.getBajoPeso().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getBajoPeso().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Respiración Rápida", f)));
            if (hojaConsulta.getRespiracionRapida() != null && !hojaConsulta.getRespiracionRapida().equals("")) {
                if (hojaConsulta.getRespiracionRapida().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Referencia por IRAG", f)));
            if (hojaConsulta.getReferenciaIrag() != null && !hojaConsulta.getReferenciaIrag().equals("")) {
                if (hojaConsulta.getReferenciaIrag().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Bajo Peso Severo", f)));
            if (hojaConsulta.getBajoPesoSevero() != null && !hojaConsulta.getBajoPesoSevero().equals("")) {
                if (hojaConsulta.getBajoPesoSevero().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getBajoPesoSevero().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Quejido Espiratorio", f)));
            if (hojaConsulta.getQuejidoEspiratorio() != null && !hojaConsulta.getQuejidoEspiratorio().equals("")) {
                if (hojaConsulta.getQuejidoEspiratorio().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Referencia CHIK", f)));
            if (hojaConsulta.getReferenciaChik() != null && !hojaConsulta.getReferenciaChik().equals("")) {
                if (hojaConsulta.getReferenciaChik().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("IMC", f)));
            table.addCell(getCell("     IMC    " + (hojaConsulta.getImc() > 0 ? hojaConsulta.getImc().toString() : ""), Element.ALIGN_LEFT));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Estridor en Reposo", f)));
            if (hojaConsulta.getEstiradorReposo() != null && !hojaConsulta.getEstiradorReposo().equals("")) {
                if (hojaConsulta.getEstiradorReposo().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("                              ETI", f)));
            if (hojaConsulta.getEti() != null && !hojaConsulta.getEti().equals("")) {
                if (hojaConsulta.getEti().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Lactancia Materna", f)));
            if (hojaConsulta.getLactanciaMaterna() != null && !hojaConsulta.getLactanciaMaterna().equals("")) {
                if (hojaConsulta.getLactanciaMaterna().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getLactanciaMaterna().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Tiraje Subcostal", f)));
            if (hojaConsulta.getTirajeSubcostal() != null && !hojaConsulta.getTirajeSubcostal().equals("")) {
                if (hojaConsulta.getTirajeSubcostal().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("                              IRAG", f)));
            if (hojaConsulta.getIrag() != null && !hojaConsulta.getIrag().equals("")) {
                if (hojaConsulta.getIrag().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Vacunas Completas", f)));
            if (hojaConsulta.getVacunasCompletas() != null && !hojaConsulta.getVacunasCompletas().equals("")) {
                if (hojaConsulta.getVacunasCompletas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getVacunasCompletas().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Sibilancias", f)));
            if (hojaConsulta.getSibilancias() != null && !hojaConsulta.getSibilancias().equals("")) {
                if (hojaConsulta.getSibilancias().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("                              Neumonía", f)));
            if (hojaConsulta.getIrag() != null && !hojaConsulta.getIrag().equals("")) {
                if (hojaConsulta.getIrag().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Vacuna Influenza", f)));
            if (hojaConsulta.getVacunaInfluenza() != null && !hojaConsulta.getVacunaInfluenza().equals("")) {
                if (hojaConsulta.getVacunaInfluenza().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getVacunaInfluenza().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Crépitos", f)));
            if (hojaConsulta.getCrepitos() != null && !hojaConsulta.getCrepitos().equals("")) {
                if (hojaConsulta.getCrepitos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }

            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            String formattedDate = "";
            if (hojaConsulta.getFechaVacuna() != null && !hojaConsulta.getFechaVacuna().equals("")) {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaConsulta());
                formattedDate = targetFormat.format(date);
                table.addCell(new PdfPCell(new Phrase("Fecha Vacuna    |  " + formattedDate, f)));
            } else {
                table.addCell(new PdfPCell(new Phrase("Fecha Vacuna    |  ", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Roncos", f)));
            if (hojaConsulta.getRoncos() != null && !hojaConsulta.getRoncos().equals("")) {
                if (hojaConsulta.getRoncos().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Otra FIF", f)));
            if (hojaConsulta.getOtraFif() != null && !hojaConsulta.getOtraFif().equals("")) {
                if (hojaConsulta.getOtraFif().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("Saturación 02:       " + (hojaConsulta.getSaturaciono2() > 0 ? hojaConsulta.getSaturaciono2() : ""), Element.ALIGN_LEFT));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            String nuevaFif = "";
            if (hojaConsulta.getNuevaFif() != null && !hojaConsulta.getNuevaFif().equals("")) {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaConsulta());
                nuevaFif = targetFormat.format(date);
                table.addCell(new PdfPCell(new Phrase("Nueva FIF    | " + nuevaFif, f)));
            } else {
                table.addCell(new PdfPCell(new Phrase("Nueva FIF    | " + nuevaFif, f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getCategoria() != null && !hojaConsulta.getCategoria().equals("")) {
                if (hojaConsulta.getCategoria().trim().equals("A")) {
                    table.addCell(getCell("Categoría: [A]    B    C     D    NA    ", Element.ALIGN_LEFT));
                } else if (hojaConsulta.getCategoria().trim().equals("B")) {
                    table.addCell(getCell("Categoría: A    [B]    C     D    NA    ", Element.ALIGN_LEFT));
                } else if (hojaConsulta.getCategoria().trim().equals("C")) {
                    table.addCell(getCell("Categoría: A    B    [C]     D    NA    ", Element.ALIGN_LEFT));
                } else if (hojaConsulta.getCategoria().trim().equals("D")) {
                    table.addCell(getCell("Categoría: A    B    C     [D]    NA    ", Element.ALIGN_LEFT));
                } else {
                    table.addCell(getCell("Categoría: A    B    C     D    [NA]    ", Element.ALIGN_LEFT));
                }
            } else {
                table.addCell(getCell("Categoría: A    B    C     D    NA    ", Element.ALIGN_LEFT));
            }

            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getCambioCategoria() != null && !hojaConsulta.getCambioCategoria().equals("")) {
                if (hojaConsulta.getCambioCategoria().equals("0")) {
                    table.addCell(getCell("Cambio de  Categoría:   [SI]   NO", Element.ALIGN_LEFT));
                } else {
                    table.addCell(getCell("Cambio de  Categoría:   SI   [NO]", Element.ALIGN_LEFT));
                }
            } else {
                table.addCell(getCell("Cambio de  Categoría:   SI   NO", Element.ALIGN_LEFT));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            /*---*/
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("Complete si es Categoría A Y B", Element.ALIGN_LEFT));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Manifestación Hemorrágica", f)));
            if (hojaConsulta.getManifestacionHemorragica() != null && !hojaConsulta.getManifestacionHemorragica().equals("")) {
                if (hojaConsulta.getManifestacionHemorragica().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Epistaxis", f)));
            if (hojaConsulta.getEpistaxis() != null && !hojaConsulta.getEpistaxis().equals("")) {
                if (hojaConsulta.getEpistaxis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase(" Hipermenorrea", f)));
            if (hojaConsulta.getHipermenorrea() != null && !hojaConsulta.getHipermenorrea().equals("")) {
                if (hojaConsulta.getHipermenorrea().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getHipermenorrea().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Prueba torniquete positiva", f)));
            if (hojaConsulta.getPruebaTorniquetePositiva() != null && !hojaConsulta.getPruebaTorniquetePositiva().equals("")) {
                if (hojaConsulta.getPruebaTorniquetePositiva().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Gingivorragia", f)));
            if (hojaConsulta.getGingivorragia() != null && !hojaConsulta.getGingivorragia().equals("")) {
                if (hojaConsulta.getGingivorragia().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase(" Hematemesis", f)));
            if (hojaConsulta.getHematemesis() != null && !hojaConsulta.getHematemesis().equals("")) {
                if (hojaConsulta.getHematemesis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Petequias >= 10 en PT", f)));
            if (hojaConsulta.getPetequia10Pt() != null && !hojaConsulta.getPetequia10Pt().equals("")) {
                if (hojaConsulta.getPetequia10Pt().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Petequias Espontáneas", f)));
            if (hojaConsulta.getPetequiasEspontaneas() != null && !hojaConsulta.getPetequiasEspontaneas().equals("")) {
                if (hojaConsulta.getPetequiasEspontaneas().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Melena", f)));
            if (hojaConsulta.getMelena() != null && !hojaConsulta.getMelena().equals("")) {
                if (hojaConsulta.getMelena().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(new PdfPCell(new Phrase("Petequias >= 20 en PT", f)));
            if (hojaConsulta.getPetequia20Pt() != null && !hojaConsulta.getPetequia20Pt().equals("")) {
                if (hojaConsulta.getPetequia20Pt().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Llenado capilar > 2 seg", f)));
            if (hojaConsulta.getLlenadoCapilar2seg() != null && !hojaConsulta.getLlenadoCapilar2seg().equals("")) {
                if (hojaConsulta.getLlenadoCapilar2seg().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Hemoconcentración " + (hojaConsulta.getHemoconcentracion() > 0 ? hojaConsulta.getHemoconcentracion() : "----") + " %", f)));
            if (hojaConsulta.getHemoconc() != null && !hojaConsulta.getHemoconc().equals("")) {
                if (hojaConsulta.getHemoconc().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else if (hojaConsulta.getHemoconc().trim().equals("1")) {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(new PdfPCell(new Phrase("Piel y extremidades frías", f)));
            if (hojaConsulta.getPielExtremidadesFrias() != null && !hojaConsulta.getPielExtremidadesFrias().equals("")) {
                if (hojaConsulta.getPielExtremidadesFrias().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Cianosis", f)));
            if (hojaConsulta.getCianosis() != null && !hojaConsulta.getCianosis().equals("")) {
                if (hojaConsulta.getCianosis().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Palidez en extremidades", f)));
            if (hojaConsulta.getPalidezEnExtremidades() != null && !hojaConsulta.getPalidezEnExtremidades().equals("")) {
                if (hojaConsulta.getPalidezEnExtremidades().trim().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getFechaLinfocitos() != null && !hojaConsulta.getFechaLinfocitos().equals("")) {
                String fechaLifocitos = "";
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaLinfocitos());
                fechaLifocitos = targetFormat.format(date);
                table.addCell(getCellLinfocitos("Lifoncito Atipicos " + hojaConsulta.getLinfocitosaAtipicos() + " % " + "Fecha: " + fechaLifocitos , Element.ALIGN_LEFT));
            } else {
                table.addCell(getCell("Lifoncito Atipicos       % " + "   Fecha : ", Element.ALIGN_LEFT));
            }
            //table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            paragraph.add(table);
            DOCUMENT.add(paragraph);
            lasPartFirstPage(hojaConsulta);

        } catch (Exception e) {
            Log.e("generarFistPage", e.toString());
        }
    }

    public void lasPartFirstPage(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            PdfPTable table = new PdfPTable(2);
            Paragraph paragraph = new Paragraph();
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(8);
            table.setWidths(new float[]{10f, 1f});
            /*---*/
            if (hojaConsulta.getHospitalizado() != null && !hojaConsulta.getHospitalizado().equals("")) {
                if (hojaConsulta.getHospitalizado().equals("0")) {
                    table.addCell(getCell("Ha sido Hospitalizado en el ultimo año [Si] NO, Si es Si Especifique: " + hojaConsulta.getHospitalizadoEspecificar(), Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(getCell("Ha sido Hospitalizado en el ultimo año Si [NO], Si es Si Especifique:", Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(getCell("Ha sido Hospitalizado en el ultimo año Si NO, Si es Si Especifique:", Element.ALIGN_LEFT));
                /*Numero de hoja de consulta*/
                //table.addCell(getCell("No " + hojaConsulta.getNumHojaConsulta(), Element.ALIGN_CENTER));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getTransfusionSangre() != null && !hojaConsulta.getTransfusionSangre().equals("")) {
                if (hojaConsulta.getTransfusionSangre().equals("0")) {
                    table.addCell(getCell("Recibió Transfusión de sangre en el último año [Si] NO, Si es Si Especifique: " + hojaConsulta.getTransfusionEspecificar(), Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(getCell("Recibió Transfusión de sangre en el último año Si [NO], Si es Si Especifique:", Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(getCell("Recibió Transfusión de sangre en el último año Si NO, Si es Si Especifique:", Element.ALIGN_LEFT));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getTomandoMedicamento() != null && !hojaConsulta.getTomandoMedicamento().equals("")) {
                if (hojaConsulta.getTomandoMedicamento().equals("0")) {
                    table.addCell(getCell("Está tomando medicamento en este momento [Si] NO, Si es Si Especifique: " + hojaConsulta.getMedicamentoEspecificar(), Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(getCell("Está tomando medicamento en este momento Si [NO], Si es Si Especifique:", Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(getCell("Está tomando medicamento en este momento Si NO, Si es Si Especifique:", Element.ALIGN_LEFT));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            if (hojaConsulta.getMedicamentoDistinto() != null && !hojaConsulta.getMedicamentoDistinto().equals("")) {
                if (hojaConsulta.getMedicamentoDistinto().equals("0")) {
                    table.addCell(getCell("Toma un medicamento distinto al mencionado en los ultimos 6 meses [Si] NO, Si es Si Especifique: " + hojaConsulta.getMedicamentoDistEspecificar(), Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                } else {
                    table.addCell(getCell("Toma un medicamento distinto al mencionado en los ultimos 6 meses Si [NO], Si es Si Especifique:", Element.ALIGN_LEFT));
                    table.addCell(getCell("", Element.ALIGN_CENTER));
                }
            } else {
                table.addCell(getCell("Toma un medicamento distinto al mencionado en los ultimos 6 meses Si NO, Si es Si Especifique:", Element.ALIGN_LEFT));
                table.addCell(getCell("", Element.ALIGN_CENTER));
            }

            PdfPTable table2 = new PdfPTable(2);
            Paragraph paragraph2 = new Paragraph();
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.setSpacingBefore(15);
            table2.setWidths(new float[]{1f, 10f});

            if (hojaConsulta.getStatusSubmitted().equals("")) {
                PdfContentByte cb = PDFWRITER.getDirectContent();
                String numHoja = String.valueOf(hojaConsulta.getNumHojaConsulta());
                Barcode39 barcode39 = new Barcode39();
                barcode39.setStartStopText(false);
                barcode39.setCode(numHoja);
                Image code39Image = barcode39.createImageWithBarcode(cb, null, BaseColor.RED);
                code39Image.setWidthPercentage(4);
                table2.addCell(getCell("", Element.ALIGN_CENTER));
                table2.addCell(getPdfCellImage(code39Image));
            } else {
                table2.addCell(getCell("", Element.ALIGN_CENTER));
                table2.addCell(getCell("", Element.ALIGN_CENTER));
            }

            table2.addCell(getCell("", Element.ALIGN_CENTER));
            table2.addCell(getCell("Revisión 16 Diciembre 2014 Versión 11", Element.ALIGN_RIGHT));

            paragraph.add(table);
            paragraph2.add(table2);
            DOCUMENT.add(paragraph);
            DOCUMENT.add(paragraph2);
            waterMark();
            secondPage(hojaConsulta);
        } catch (Exception e) {
            Log.e("lasPartFirstPage", e.toString());
        }
    }

    public void secondPage(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            DOCUMENT.newPage();
            PdfPTable table = new PdfPTable(4);
            Paragraph paragraph = new Paragraph();
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(15);
            //table.setWidths(new float[]{2f, 0.2f, 0.2f, 0.2f, 6f});
            table.setWidths(new float[]{2f, 0.2f, 0.2f, 0.2f});
            table.addCell(getCell2("Exámenes", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("S", f)));
            table.addCell(new PdfPCell(new Phrase("N", f)));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell("Historia y Exámen Físico", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("BHC", f)));
            if (hojaConsulta.getBhc() != null && !hojaConsulta.getBhc().equals("")) {
                if (hojaConsulta.getBhc().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("Serología para Dengue", f)));
            if (hojaConsulta.getSerologiaDengue() != null && !hojaConsulta.getSerologiaDengue().equals("")) {
                if (hojaConsulta.getSerologiaDengue().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Serología para CHIK", f)));
            if (hojaConsulta.getSerologiaChik() != null && !hojaConsulta.getSerologiaChik().equals("")) {
                if (hojaConsulta.getSerologiaChik().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Gota Gruesa", f)));
            if (hojaConsulta.getGotaGruesa() != null && !hojaConsulta.getGotaGruesa().equals("")) {
                if (hojaConsulta.getGotaGruesa().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Extendido Periférico", f)));
            if (hojaConsulta.getExtendidoPeriferico() != null && !hojaConsulta.getExtendidoPeriferico().equals("")) {
                if (hojaConsulta.getExtendidoPeriferico().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("EGO", f)));
            if (hojaConsulta.getEgo() != null && !hojaConsulta.getEgo().equals("")) {
                if (hojaConsulta.getEgo().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("EGH", f)));
            if (hojaConsulta.getEgh() != null && !hojaConsulta.getEgh().equals("")) {
                if (hojaConsulta.getEgh().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Citología Fecal", f)));
            if (hojaConsulta.getCitologiaFecal() != null && !hojaConsulta.getCitologiaFecal().equals("")) {
                if (hojaConsulta.getCitologiaFecal().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Factor Reumatoídeo", f)));
            if (hojaConsulta.getFactorReumatoideo() != null && !hojaConsulta.getFactorReumatoideo().equals("")) {
                if (hojaConsulta.getFactorReumatoideo().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Albumina", f)));
            if (hojaConsulta.getAlbumina() != null && !hojaConsulta.getAlbumina().equals("")) {
                if (hojaConsulta.getAlbumina().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("AST/ALT", f)));
            if (hojaConsulta.getAstAlt() != null && !hojaConsulta.getAstAlt().equals("")) {
                if (hojaConsulta.getAstAlt().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Bilirubinas", f)));
            if (hojaConsulta.getBilirrubinas() != null && !hojaConsulta.getBilirrubinas().equals("")) {
                if (hojaConsulta.getBilirrubinas().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("CPK", f)));
            if (hojaConsulta.getCpk() != null && !hojaConsulta.getCpk().equals("")) {
                if (hojaConsulta.getCpk().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Colesterol", f)));
            if (hojaConsulta.getColesterol() != null && !hojaConsulta.getColesterol().equals("")) {
                if (hojaConsulta.getColesterol().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Influenza", f)));
            if (hojaConsulta.getInfluenza() != null && !hojaConsulta.getInfluenza().equals("")) {
                if (hojaConsulta.getInfluenza().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Otros: " + ((hojaConsulta.getOtroExamenLab() != null && !hojaConsulta.getOtroExamenLab().equals("")) ? hojaConsulta.getOtroExamenLab() : "-----"), f)));
            if (hojaConsulta.getOel() != null && !hojaConsulta.getInfluenza().equals("")) {
                if (hojaConsulta.getInfluenza().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));

            table.addCell(getCell2("Tratamiento", Element.ALIGN_CENTER));
            table.addCell(new PdfPCell(new Phrase("S", f)));
            table.addCell(new PdfPCell(new Phrase("N", f)));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Acetaminofén", f)));
            if (hojaConsulta.getAcetaminofen() != null && !hojaConsulta.getAcetaminofen().equals("")) {
                if (hojaConsulta.getAcetaminofen().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("ASA", f)));
            if (hojaConsulta.getAsa() != null && !hojaConsulta.getAsa().equals("")) {
                if (hojaConsulta.getAsa().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Ibuprofén", f)));
            if (hojaConsulta.getIbuprofen() != null && !hojaConsulta.getIbuprofen().equals("")) {
                if (hojaConsulta.getIbuprofen().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            table.addCell(getCellMerge("Antibióticos", Element.ALIGN_LEFT));
            /*table.addCell(new PdfPCell(new Phrase("Antibióticos", f)));
            table.addCell(new PdfPCell(new Phrase("", f)));
            table.addCell(new PdfPCell(new Phrase("", f)));*/
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("                              Penicilina", f)));
            if (hojaConsulta.getPenicilina() != null && !hojaConsulta.getPenicilina().equals("")) {
                if (hojaConsulta.getPenicilina().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("                              Amoxicilina", f)));
            if (hojaConsulta.getAmoxicilina() != null && !hojaConsulta.getAmoxicilina().equals("")) {
                if (hojaConsulta.getAmoxicilina().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("                              Dicloxacilina", f)));
            if (hojaConsulta.getDicloxacilina() != null && !hojaConsulta.getDicloxacilina().equals("")) {
                if (hojaConsulta.getDicloxacilina().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Otro: " + ((hojaConsulta.getOtroAntibiotico() != null && !hojaConsulta.getOtroAntibiotico().equals("")) ? hojaConsulta.getOtroAntibiotico() : "----"), f)));
            if (hojaConsulta.getOtro() != null && !hojaConsulta.getOtro().equals("")) {
                if (hojaConsulta.getOtro().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Furazolidona", f)));
            if (hojaConsulta.getFurazolidona() != null && !hojaConsulta.getFurazolidona().equals("")) {
                if (hojaConsulta.getFurazolidona().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Metronidazol/Tinidazol", f)));
            if (hojaConsulta.getMetronidazolTinidazol() != null && !hojaConsulta.getMetronidazolTinidazol().equals("")) {
                if (hojaConsulta.getMetronidazolTinidazol().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Albendazol / Mebendazol", f)));
            if (hojaConsulta.getAlbendazolMebendazol() != null && !hojaConsulta.getAlbendazolMebendazol().equals("")) {
                if (hojaConsulta.getAlbendazolMebendazol().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Sulfato ferroso", f)));
            if (hojaConsulta.getSulfatoFerroso() != null && !hojaConsulta.getSulfatoFerroso().equals("")) {
                if (hojaConsulta.getSulfatoFerroso().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Suero Oral", f)));
            if (hojaConsulta.getSueroOral() != null && !hojaConsulta.getSueroOral().equals("")) {
                if (hojaConsulta.getSueroOral().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Sulfato de Zinc", f)));
            if (hojaConsulta.getSulfatoZinc() != null && !hojaConsulta.getSulfatoZinc().equals("")) {
                if (hojaConsulta.getSulfatoZinc().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Líquidos IV", f)));
            if (hojaConsulta.getLiquidosIv() != null && !hojaConsulta.getLiquidosIv().equals("")) {
                if (hojaConsulta.getLiquidosIv().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Prednisona", f)));
            if (hojaConsulta.getPrednisona() != null && !hojaConsulta.getPrednisona().equals("")) {
                if (hojaConsulta.getPrednisona().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Hidrocortisona IV", f)));
            if (hojaConsulta.getHidrocortisonaIv() != null && !hojaConsulta.getHidrocortisonaIv().equals("")) {
                if (hojaConsulta.getHidrocortisonaIv().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Salbutamol", f)));
            if (hojaConsulta.getSalbutamol() != null && !hojaConsulta.getSalbutamol().equals("")) {
                if (hojaConsulta.getSalbutamol().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));
            table.addCell(new PdfPCell(new Phrase("Oseltamivir", f)));
            if (hojaConsulta.getOseltamivir() != null && !hojaConsulta.getOseltamivir().equals("")) {
                if (hojaConsulta.getOseltamivir().equals("0")) {
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                    table.addCell(new PdfPCell(new Phrase("", f)));
                } else {
                    table.addCell(new PdfPCell(new Phrase("", f)));
                    table.addCell(new PdfPCell(new Phrase("X", f)));
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(getCell3("", Element.ALIGN_LEFT));

            PdfPTable table2 = new PdfPTable(1);
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.setSpacingBefore(15);
            //table.setWidths(new float[]{2f, 0.2f, 0.2f, 0.2f, 6f});
            table2.setWidths(new float[]{6f});
            table2.addCell(getCell("Historia y Exámen Físico", Element.ALIGN_CENTER));
            if (hojaConsulta.getHistoriaExamenFisico() != null && !hojaConsulta.getHistoriaExamenFisico().equals("")) {
                table2.addCell(getCell3(hojaConsulta.getHistoriaExamenFisico(), Element.ALIGN_LEFT));
            } else {
                table2.addCell(getCell3("", Element.ALIGN_LEFT));
            }

            PdfPTable table3 = new PdfPTable(2);
            table3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.setSpacingBefore(0);
            table3.setWidths(new float[]{3f,7f});
            table3.addCell(getCellTable(table));
            table3.addCell(getCellTable(table2));

            paragraph.add(table3);
            DOCUMENT.add(paragraph);
            planes(hojaConsulta);
        } catch (Exception e) {
            Log.e("secondPage", e.toString());
        }
    }

    public void planes(HojaConsultaOffLineDTO hojaConsulta) {
        try {
            Paragraph paragraph = new Paragraph();
            PdfPTable table = new PdfPTable(2);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(15);
            table.setWidths(new float[]{4f, 1f});
            table.addCell(getCell("Planes", Element.ALIGN_CENTER));
            table.addCell(getCell("", Element.ALIGN_CENTER));
            //table.addCell(new PdfPCell(new Phrase("", f)));
            if (hojaConsulta.getPlanes() != null && !hojaConsulta.getPlanes().equals("")) {
                table.addCell(new PdfPCell(new Phrase(hojaConsulta.getPlanes(), f)));
            } else {
                table.addCell(new PdfPCell(new Phrase("", f)));
            }
            table.addCell(getCell("", Element.ALIGN_CENTER));


            PdfPTable table2 = new PdfPTable(1);
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.setSpacingBefore(15);
            table2.setWidths(new float[]{7f});
            table2.addCell(getCell("Diagnóstico", Element.ALIGN_CENTER));
            if (hojaConsulta.getDiagnostico1() > 0) {
                DiagnosticoDTO diagnostico = new DiagnosticoDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                diagnostico = mDbAdapter.getDiagnostico(MainDBConstants.secDiagnostico  + "='" + hojaConsulta.getDiagnostico1() + "'", null);
                if (diagnostico.getDiagnostico().trim().equals("Otro Diagnóstico")) {
                    table2.addCell(getCellMarginTop("1. Otro Diagnóstico: " +hojaConsulta.getOtroDiagnostico(), Element.ALIGN_LEFT));
                } else {
                    table2.addCell(getCellMarginTop("1. " +diagnostico.getDiagnostico(), Element.ALIGN_LEFT));
                }
            } else {
                table2.addCell(getCellMarginTop("1. "+"----------------------------------------", Element.ALIGN_LEFT));
            }
            if (hojaConsulta.getDiagnostico2() > 0) {
                DiagnosticoDTO diagnostico = new DiagnosticoDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                diagnostico = mDbAdapter.getDiagnostico(MainDBConstants.secDiagnostico  + "='" + hojaConsulta.getDiagnostico2() + "'", null);
                if (diagnostico.getDiagnostico().trim().equals("Otro Diagnóstico")) {
                    table2.addCell(getCellMarginTop("2. Otro Diagnóstico: " +hojaConsulta.getOtroDiagnostico(), Element.ALIGN_LEFT));
                } else {
                    table2.addCell(getCellMarginTop("2. " +diagnostico.getDiagnostico(), Element.ALIGN_LEFT));
                }
            } else {
                table2.addCell(getCellMarginTop("2. "+"----------------------------------------", Element.ALIGN_LEFT));
            }
            if (hojaConsulta.getDiagnostico3() > 0) {
                DiagnosticoDTO diagnostico = new DiagnosticoDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                diagnostico = mDbAdapter.getDiagnostico(MainDBConstants.secDiagnostico  + "='" + hojaConsulta.getDiagnostico3() + "'", null);
                if (diagnostico.getDiagnostico().trim().equals("Otro Diagnóstico")) {
                    table2.addCell(getCellMarginTop("3. Otro Diagnóstico: " +hojaConsulta.getOtroDiagnostico(), Element.ALIGN_LEFT));
                } else {
                    table2.addCell(getCellMarginTop("3. " +diagnostico.getDiagnostico(), Element.ALIGN_LEFT));
                }
            } else {
                table2.addCell(getCellMarginTop("3. "+"----------------------------------------", Element.ALIGN_LEFT));
            }
            if (hojaConsulta.getDiagnostico4() > 0) {
                DiagnosticoDTO diagnostico = new DiagnosticoDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                diagnostico = mDbAdapter.getDiagnostico(MainDBConstants.secDiagnostico  + "='" + hojaConsulta.getDiagnostico4() + "'", null);
                if (diagnostico.getDiagnostico().trim().equals("Otro Diagnóstico")) {
                    table2.addCell(getCellMarginTop("4. Otro Diagnóstico: " +hojaConsulta.getOtroDiagnostico(), Element.ALIGN_LEFT));
                } else {
                    table2.addCell(getCellMarginTop("4. " +diagnostico.getDiagnostico(), Element.ALIGN_LEFT));
                }
            } else {
                table2.addCell(getCellMarginTop("4. "+"----------------------------------------", Element.ALIGN_LEFT));
            }

            String proximaCita = "----------";
            String telef = "----------";
            if (hojaConsulta.getProximaCita() != null && !hojaConsulta.getProximaCita().equals("")) {
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getProximaCita());
                proximaCita = targetFormat.format(date);
            }
            if (hojaConsulta.getTelef() > 0 && hojaConsulta.getTelef() != null) {
                telef = hojaConsulta.getTelef().toString();
            }
            table2.addCell(getCellMarginTop("Telf. Emerg: " + telef + "      Próxima Cita: " + proximaCita, Element.ALIGN_LEFT));

            if (hojaConsulta.getColegio() != null && !hojaConsulta.getColegio().equals("")) {
                EscuelaPacienteDTO escuelaPaciente = new EscuelaPacienteDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                escuelaPaciente = mDbAdapter.getEscuela(MainDBConstants.codEscuela  + "='" + hojaConsulta.getColegio() + "'", null);
                table2.addCell(getCellMarginTop("Colegio: " + escuelaPaciente.getDescripcion(), Element.ALIGN_LEFT));
            } else {
                table2.addCell(getCellMarginTop("Colegio: ----------------------------------------", Element.ALIGN_LEFT));
            }

            if (hojaConsulta.getHorarioClases() != null && !hojaConsulta.getHorarioClases().equals("")) {
                if (hojaConsulta.getHorarioClases().trim().equals("M")) {
                    table2.addCell(getCellMarginTop("Horario de Clases:  [AM]    PM    NA", Element.ALIGN_LEFT));
                } else if (hojaConsulta.getHorarioClases().trim().equals("V")) {
                    table2.addCell(getCellMarginTop("Horario de Clases:  AM    [PM]    NA", Element.ALIGN_LEFT));
                } else {
                    table2.addCell(getCellMarginTop("Horario de Clases:  AM    PM    [NA]", Element.ALIGN_LEFT));
                }
            } else {
                table2.addCell(getCellMarginTop("Horario de Clases:  AM    PM    NA", Element.ALIGN_LEFT));
            }

            PdfPTable table3 = new PdfPTable(5);
            table3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table3.setSpacingBefore(15);
            table3.setWidths(new float[]{3f,1f,1.4f,1.2f,3f});
            table3.addCell(getCell4("Cargo", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Número", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Fecha", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Hora", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Firma", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Médico", Element.ALIGN_CENTER));
            if (hojaConsulta.getUsuarioMedico() > 0) {
                UsuarioDTO usuario = new UsuarioDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                usuario = mDbAdapter.getUsuario(MainDBConstants.id  + "='" + hojaConsulta.getUsuarioMedico() + "'", null);
                if (usuario.getCodigoPersonal() != null && !usuario.getCodigoPersonal().equals("")) {
                    table3.addCell(getCell4(usuario.getCodigoPersonal(), Element.ALIGN_CENTER));
                } else {
                    table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
                }
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            if (hojaConsulta.getFechaCierre() != null && !hojaConsulta.getFechaCierre().equals("")) {
                String fechaCierre = "";
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaCierre());
                fechaCierre = targetFormat.format(date);
                table3.addCell(getCell4(fechaCierre, Element.ALIGN_CENTER));
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            if (hojaConsulta.getFechaCierre() != null && !hojaConsulta.getFechaCierre().equals("")) {
                String horaCierre = "";
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaCierre());
                horaCierre = targetFormat.format(date);
                table3.addCell(getCell4(horaCierre, Element.ALIGN_CENTER));
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            if (hojaConsulta.getUsuarioMedico() > 0) {
                UsuarioDTO usuario = new UsuarioDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                usuario = mDbAdapter.getUsuario(MainDBConstants.id  + "='" + hojaConsulta.getUsuarioMedico() + "'", null);
                table3.addCell(getCell4(usuario.getNombre(), Element.ALIGN_CENTER));
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            table3.addCell(getCell4("Médico Cambio Turno", Element.ALIGN_CENTER));
            table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            table3.addCell(getCell4("Enfermería", Element.ALIGN_CENTER));
            if (hojaConsulta.getUsuarioEnfermeria() > 0) {
                UsuarioDTO usuario = new UsuarioDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                usuario = mDbAdapter.getUsuario(MainDBConstants.id  + "='" + hojaConsulta.getUsuarioEnfermeria() + "'", null);
                if (usuario.getCodigoPersonal() != null && !usuario.getCodigoPersonal().equals("")) {
                    table3.addCell(getCell4(usuario.getCodigoPersonal(), Element.ALIGN_CENTER));
                } else {
                    table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
                }
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            if (hojaConsulta.getFechaCierre() != null && !hojaConsulta.getFechaCierre().equals("")) {
                String fechaCierre = "";
                DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Date date = null;
                date = originalFormat.parse(hojaConsulta.getFechaCierre());
                fechaCierre = targetFormat.format(date);
                table3.addCell(getCell4(fechaCierre, Element.ALIGN_CENTER));
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }
            table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            if (hojaConsulta.getUsuarioEnfermeria() > 0) {
                UsuarioDTO usuario = new UsuarioDTO();
                mDbAdapter = new HojaConsultaDBAdapter(CONTEXT, false,false);
                mDbAdapter.open();
                usuario = mDbAdapter.getUsuario(MainDBConstants.id  + "='" + hojaConsulta.getUsuarioEnfermeria() + "'", null);
                table3.addCell(getCell4(usuario.getNombre(), Element.ALIGN_CENTER));
            } else {
                table3.addCell(getCell4("-----", Element.ALIGN_CENTER));
            }

            table3.addCell(getCell4("Supervisor", Element.ALIGN_CENTER));
            table3.addCell(getCell4("", Element.ALIGN_CENTER));
            table3.addCell(getCell4("", Element.ALIGN_CENTER));
            table3.addCell(getCell4("", Element.ALIGN_CENTER));
            table3.addCell(getCell4("", Element.ALIGN_CENTER));

            table2.addCell(getCellTable(table3));


            PdfPTable table4 = new PdfPTable(2);
            table4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table4.setSpacingBefore(0);
            table4.setWidths(new float[]{4f,7f});
            table4.addCell(getCellTable(table));
            table4.addCell(getCellTable(table2));

            paragraph.add(table4);
            DOCUMENT.add(paragraph);
            footer();
        } catch (Exception e) {
            Log.e("planes", e.toString());
        }
    }

    public void footer() {
        try {
            PdfPTable table = new PdfPTable(2);
            Paragraph paragraph = new Paragraph();
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(15);
            table.setWidths(new float[]{3f,3f});
            table.addCell(getCellMarginTop("Sellos Digitación", Element.ALIGN_CENTER));
            table.addCell(getCellMarginTop("Sellos Médico y Supervisor", Element.ALIGN_CENTER));

            table.addCell(getCellMarginTop("", Element.ALIGN_LEFT));
            table.addCell(getCellMarginTop("Revisión 16 Diciembre 2014 Versión 11", Element.ALIGN_CENTER));
            paragraph.add(table);
            DOCUMENT.add(paragraph);
            waterMark();
        } catch (Exception e) {
            Log.e("footer", e.toString());
        }
    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellLinfocitos(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(0);
        cell.setColspan(3);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellMarginTop(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(0);
        cell.setPaddingTop(10);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCellTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell(table);
        cell.setPadding(0);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getPdfCellImage(Image image) {
        PdfPCell cell = new PdfPCell(image);
        cell.setPadding(0);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(2);
        return cell;
    }

    public PdfPCell getCell2(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public PdfPCell getCell3(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public PdfPCell getCell4(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(2);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public PdfPCell getCellMerge(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setPadding(2);
        cell.setColspan(3);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public void waterMark() {
        try {
            // Agregar marca de agua
            PdfContentByte waterMark = PDFWRITER.getDirectContentUnder();
            waterMark.beginText();
            // Establecer transparencia de marca de agua
            PdfGState gs = new PdfGState();
            // Establece la opacidad de la fuente
            gs.setFillOpacity(0.1f);
            // Parámetros de fuente, formato de codificación de fuente,
            // si incrustar información de fuente en pdf (generalmente no es necesario incrustar), tamaño de fuente)
            waterMark.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252,
                    BaseFont.NOT_EMBEDDED), 50);
            // Establecer la transparencia
            waterMark.setGState(gs);
            // Establecer la alineación de la marca de agua Contenido de la marca de agua Coordenada X Coordenada Y Ángulo de rotación
            waterMark.showTextAligned (Element.ALIGN_CENTER, "DOCUMENTO NO OFICIAL", 300, 500, 40);
            // Establecer el color de la marca de agua
            waterMark.setColorFill(BaseColor.GRAY);
            // Finalizar la configuración
            waterMark.endText();
            waterMark.stroke();
        } catch (Exception e) {
            Log.e("warterMar", e.toString());
        }
    }
}
