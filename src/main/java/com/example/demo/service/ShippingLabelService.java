package com.example.demo.service;

import com.example.demo.entity.OrderInfo;
import com.example.demo.entity.OrderItemInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShippingLabelService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final float[] BORDER_COLOR = new float[] {0.78f, 0.10f, 0.10f};
    private static final float PAGE_MARGIN = 18f;
    private static final float FONT_SIZE_SMALL = 7f;
    private static final float FONT_SIZE_BODY = 8f;
    private static final float FONT_SIZE_SECTION = 10f;
    private static final float FONT_SIZE_HEADLINE = 14f;

    public ShippingLabelDocument generateLabel(OrderInfo orderInfo) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth()));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float contentWidth = pageWidth - (PAGE_MARGIN * 2);
                float contentHeight = pageHeight - (PAGE_MARGIN * 2);
                float x = PAGE_MARGIN;
                float y = PAGE_MARGIN;

                drawBorder(contentStream, x, y, contentWidth, contentHeight);
                drawHeader(contentStream, orderInfo, x, pageHeight - PAGE_MARGIN, contentWidth);
                drawAddressSection(contentStream, orderInfo, x, pageHeight - PAGE_MARGIN - 28, contentWidth);
                drawOrderMetaSection(contentStream, orderInfo, x, pageHeight - PAGE_MARGIN - 114, contentWidth);
                drawPaymentAndPackSection(contentStream, orderInfo, x, pageHeight - PAGE_MARGIN - 164, contentWidth);
                drawItemsTable(contentStream, orderInfo, x, pageHeight - PAGE_MARGIN - 214, contentWidth);
                drawFooter(contentStream, x, y + 8, contentWidth);
            }

            document.save(outputStream);
            return new ShippingLabelDocument(orderInfo.getOrderNumber() + "-shipping-label.pdf", outputStream.toByteArray());
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to generate shipping label PDF",
                    exception
            );
        }
    }

    private void drawHeader(
            PDPageContentStream contentStream,
            OrderInfo orderInfo,
            float x,
            float topY,
            float width
    ) throws IOException {
        float headerHeight = 28f;
        float bottomY = topY - headerHeight;
        drawBorder(contentStream, x, bottomY, width, headerHeight);

        writeText(contentStream, "SHIPPING LABEL", x + 10, topY - 12, FONT_BOLD, FONT_SIZE_HEADLINE);
        writeText(
                contentStream,
                "Packed order copy",
                x + width - 105,
                topY - 12,
                FONT_BOLD,
                FONT_SIZE_BODY
        );
        writeText(
                contentStream,
                "Generated " + DATE_TIME_FORMATTER.format(orderInfo.getShippingLabelGeneratedAt() == null
                        ? orderInfo.getPackedAt()
                        : orderInfo.getShippingLabelGeneratedAt()),
                x + width - 105,
                topY - 22,
                FONT_REGULAR,
                FONT_SIZE_SMALL
        );
    }

    private void drawAddressSection(
            PDPageContentStream contentStream,
            OrderInfo orderInfo,
            float x,
            float topY,
            float width
    ) throws IOException {
        float height = 86f;
        float bottomY = topY - height;
        float leftWidth = width * 0.58f;
        float rightWidth = width - leftWidth;

        drawBorder(contentStream, x, bottomY, width, height);
        drawVerticalDivider(contentStream, x + leftWidth, bottomY, bottomY + height);

        writeText(contentStream, "DELIVER TO:", x + 8, topY - 10, FONT_BOLD, FONT_SIZE_BODY);
        float deliverY = topY - 22;
        for (String line : buildDeliverToLines(orderInfo)) {
            writeText(contentStream, line, x + 8, deliverY, FONT_REGULAR, FONT_SIZE_BODY);
            deliverY -= 10;
        }

        float rightX = x + leftWidth + 8;
        writeText(contentStream, "SHIP FROM:", rightX, topY - 10, FONT_BOLD, FONT_SIZE_BODY);
        float shipFromY = topY - 22;
        for (String line : buildShipFromLines(orderInfo)) {
            writeText(contentStream, line, rightX, shipFromY, FONT_REGULAR, FONT_SIZE_BODY);
            shipFromY -= 10;
        }
    }

    private void drawOrderMetaSection(
            PDPageContentStream contentStream,
            OrderInfo orderInfo,
            float x,
            float topY,
            float width
    ) throws IOException {
        float height = 50f;
        float bottomY = topY - height;
        float leftWidth = width * 0.52f;
        float rightWidth = width - leftWidth;

        drawBorder(contentStream, x, bottomY, width, height);
        drawVerticalDivider(contentStream, x + leftWidth, bottomY, bottomY + height);

        writeText(contentStream, "ORDER ID: " + orderInfo.getOrderNumber(), x + 8, topY - 12, FONT_BOLD, FONT_SIZE_BODY);
        drawSimpleBarcode(contentStream, orderInfo.getOrderNumber(), x + 8, bottomY + 10, leftWidth - 16, 18f);

        float rightX = x + leftWidth + 8;
        writeText(contentStream, "STATUS: " + orderInfo.getStatus(), rightX, topY - 12, FONT_BOLD, FONT_SIZE_BODY);
        writeText(
                contentStream,
                "PACKED AT: " + DATE_TIME_FORMATTER.format(orderInfo.getPackedAt()),
                rightX,
                topY - 24,
                FONT_REGULAR,
                FONT_SIZE_BODY
        );
        if (orderInfo.getPicker() != null) {
            writeText(
                    contentStream,
                    "PICKER: " + orderInfo.getPicker().getName(),
                    rightX,
                    topY - 36,
                    FONT_REGULAR,
                    FONT_SIZE_BODY
            );
        }
        writeText(
                contentStream,
                "CLIENT: " + sanitize(orderInfo.getClient().getName()),
                rightX,
                topY - 46,
                FONT_REGULAR,
                FONT_SIZE_BODY
        );
    }

    private void drawPaymentAndPackSection(
            PDPageContentStream contentStream,
            OrderInfo orderInfo,
            float x,
            float topY,
            float width
    ) throws IOException {
        float height = 50f;
        float bottomY = topY - height;
        float leftWidth = width * 0.47f;
        float centerWidth = width * 0.24f;
        float rightWidth = width - leftWidth - centerWidth;

        drawBorder(contentStream, x, bottomY, width, height);
        drawVerticalDivider(contentStream, x + leftWidth, bottomY, bottomY + height);
        drawVerticalDivider(contentStream, x + leftWidth + centerWidth, bottomY, bottomY + height);

        writeText(
                contentStream,
                "SHIPMENT WEIGHT: " + formatWeight(orderInfo.getPackedWeight()),
                x + 8,
                topY - 12,
                FONT_BOLD,
                FONT_SIZE_BODY
        );
        writeText(
                contentStream,
                "BOX CATEGORY: " + orderInfo.getBoxCategory(),
                x + 8,
                topY - 24,
                FONT_REGULAR,
                FONT_SIZE_BODY
        );
        writeText(
                contentStream,
                "BOX ID: " + orderInfo.getBoxId(),
                x + 8,
                topY - 36,
                FONT_REGULAR,
                FONT_SIZE_BODY
        );

        writeText(contentStream, "PREPAID", x + leftWidth + 16, topY - 25, FONT_BOLD, FONT_SIZE_SECTION);

        float rightX = x + leftWidth + centerWidth + 8;
        writeText(contentStream, "WAREHOUSE: " + orderInfo.getWarehouseId(), rightX, topY - 12, FONT_BOLD, FONT_SIZE_BODY);
        writeText(contentStream, "FC: " + sanitize(orderInfo.getFcId()), rightX, topY - 24, FONT_REGULAR, FONT_SIZE_BODY);
        writeText(contentStream, sanitize(orderInfo.getFcLocation()), rightX, topY - 36, FONT_REGULAR, FONT_SIZE_BODY);
    }

    private void drawItemsTable(
            PDPageContentStream contentStream,
            OrderInfo orderInfo,
            float x,
            float topY,
            float width
    ) throws IOException {
        float height = 120f;
        float bottomY = topY - height;
        float skuWidth = width * 0.20f;
        float itemWidth = width * 0.48f;
        float qtyWidth = width * 0.12f;
        float orderedWidth = width - skuWidth - itemWidth - qtyWidth;
        float rowHeight = 16f;

        drawBorder(contentStream, x, bottomY, width, height);
        drawHorizontalDivider(contentStream, x, x + width, topY - rowHeight);
        drawVerticalDivider(contentStream, x + skuWidth, bottomY, topY);
        drawVerticalDivider(contentStream, x + skuWidth + itemWidth, bottomY, topY);
        drawVerticalDivider(contentStream, x + skuWidth + itemWidth + qtyWidth, bottomY, topY);

        writeCenteredText(contentStream, "SKU", x, x + skuWidth, topY - 11, FONT_BOLD, FONT_SIZE_SMALL);
        writeCenteredText(contentStream, "ITEM", x + skuWidth, x + skuWidth + itemWidth, topY - 11, FONT_BOLD, FONT_SIZE_SMALL);
        writeCenteredText(contentStream, "QTY", x + skuWidth + itemWidth, x + skuWidth + itemWidth + qtyWidth, topY - 11, FONT_BOLD, FONT_SIZE_SMALL);
        writeCenteredText(contentStream, "ORDERED", x + skuWidth + itemWidth + qtyWidth, x + width, topY - 11, FONT_BOLD, FONT_SIZE_SMALL);

        float rowTop = topY - rowHeight;
        for (OrderItemInfo item : orderInfo.getItems()) {
            if (rowTop - rowHeight < bottomY + 18) {
                break;
            }

            drawHorizontalDivider(contentStream, x, x + width, rowTop - rowHeight);
            writeText(contentStream, truncate(item.getSku(), 18), x + 4, rowTop - 11, FONT_REGULAR, FONT_SIZE_SMALL);
            writeText(contentStream, truncate(item.getProductId(), 34), x + skuWidth + 4, rowTop - 11, FONT_REGULAR, FONT_SIZE_SMALL);
            writeCenteredText(
                    contentStream,
                    String.valueOf(item.getFulfilledQuantity()),
                    x + skuWidth + itemWidth,
                    x + skuWidth + itemWidth + qtyWidth,
                    rowTop - 11,
                    FONT_REGULAR,
                    FONT_SIZE_SMALL
            );
            writeCenteredText(
                    contentStream,
                    String.valueOf(item.getQuantity()),
                    x + skuWidth + itemWidth + qtyWidth,
                    x + width,
                    rowTop - 11,
                    FONT_REGULAR,
                    FONT_SIZE_SMALL
            );
            rowTop -= rowHeight;
        }
    }

    private void drawFooter(
            PDPageContentStream contentStream,
            float x,
            float y,
            float width
    ) throws IOException {
        float termsTop = y + 18;
        drawHorizontalDivider(contentStream, x, x + width, termsTop);
        writeText(contentStream, "TERMS AND CONDITIONS:", x + 6, termsTop - 8, FONT_BOLD, FONT_SIZE_SMALL);
        writeText(contentStream, "1. Handle this label with the packed order until delivery.", x + 6, termsTop - 18, FONT_REGULAR, FONT_SIZE_SMALL);
        writeText(contentStream, "2. This label is auto-generated from current order data and does not require signature.", x + 6, termsTop - 28, FONT_REGULAR, FONT_SIZE_SMALL);
    }

    private List<String> buildDeliverToLines(OrderInfo orderInfo) {
        List<String> lines = new ArrayList<>();
        lines.addAll(splitAddressLines(orderInfo.getShipToAddress(), 30));
        lines.add("CLIENT: " + truncate(orderInfo.getClient().getName(), 24));
        lines.add("EMAIL: " + truncate(orderInfo.getClient().getEmail(), 28));
        lines.add("PHONE: " + truncate(orderInfo.getClient().getPhone(), 28));
        return lines;
    }

    private List<String> buildShipFromLines(OrderInfo orderInfo) {
        List<String> lines = new ArrayList<>();
        lines.add(truncate(orderInfo.getWarehouseId(), 28));
        lines.add("FC: " + truncate(orderInfo.getFcId(), 24));
        lines.addAll(splitAddressLines(orderInfo.getFcLocation(), 24));
        if (orderInfo.getClient().getOrganisationName() != null && !orderInfo.getClient().getOrganisationName().isBlank()) {
            lines.add("REF: " + truncate(orderInfo.getClient().getOrganisationName(), 24));
        }
        return lines;
    }

    private List<String> splitAddressLines(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        String safeText = sanitize(text);
        String[] tokens = safeText.split("[, ]+");
        StringBuilder current = new StringBuilder();
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            if (current.isEmpty()) {
                current.append(token);
                continue;
            }
            if (current.length() + 1 + token.length() > maxLength) {
                lines.add(current.toString());
                current = new StringBuilder(token);
            } else {
                current.append(' ').append(token);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private String formatWeight(BigDecimal packedWeight) {
        return packedWeight == null ? "NA" : packedWeight.stripTrailingZeros().toPlainString() + " lb";
    }

    private void drawSimpleBarcode(
            PDPageContentStream contentStream,
            String source,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {
        byte[] bytes = sanitize(source).getBytes(StandardCharsets.US_ASCII);
        float barX = x;
        float unitWidth = Math.max(1f, width / Math.max(24, bytes.length * 3f));

        contentStream.setNonStrokingColor(0f, 0f, 0f);
        for (byte value : bytes) {
            int normalized = value & 0xFF;
            int barCount = 2 + (normalized % 3);
            int gapCount = 1 + ((normalized / 3) % 2);
            for (int index = 0; index < barCount && barX < x + width; index++) {
                contentStream.addRect(barX, y, unitWidth, height);
                contentStream.fill();
                barX += unitWidth;
            }
            barX += unitWidth * gapCount;
        }
    }

    private void drawBorder(
            PDPageContentStream contentStream,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {
        contentStream.setStrokingColor(BORDER_COLOR[0], BORDER_COLOR[1], BORDER_COLOR[2]);
        contentStream.setLineWidth(0.8f);
        contentStream.addRect(x, y, width, height);
        contentStream.stroke();
    }

    private void drawVerticalDivider(
            PDPageContentStream contentStream,
            float x,
            float bottomY,
            float topY
    ) throws IOException {
        contentStream.setStrokingColor(BORDER_COLOR[0], BORDER_COLOR[1], BORDER_COLOR[2]);
        contentStream.setLineWidth(0.6f);
        contentStream.moveTo(x, bottomY);
        contentStream.lineTo(x, topY);
        contentStream.stroke();
    }

    private void drawHorizontalDivider(
            PDPageContentStream contentStream,
            float leftX,
            float rightX,
            float y
    ) throws IOException {
        contentStream.setStrokingColor(BORDER_COLOR[0], BORDER_COLOR[1], BORDER_COLOR[2]);
        contentStream.setLineWidth(0.6f);
        contentStream.moveTo(leftX, y);
        contentStream.lineTo(rightX, y);
        contentStream.stroke();
    }

    private void writeText(
            PDPageContentStream contentStream,
            String value,
            float x,
            float y,
            PDType1Font font,
            float fontSize
    ) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(0f, 0f, 0f);
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(sanitize(value));
        contentStream.endText();
    }

    private void writeCenteredText(
            PDPageContentStream contentStream,
            String value,
            float leftX,
            float rightX,
            float y,
            PDType1Font font,
            float fontSize
    ) throws IOException {
        String safeValue = sanitize(value);
        float textWidth = font.getStringWidth(safeValue) / 1000f * fontSize;
        float x = leftX + ((rightX - leftX - textWidth) / 2f);
        writeText(contentStream, safeValue, x, y, font, fontSize);
    }

    private String truncate(String value, int maxLength) {
        String safeValue = sanitize(value == null ? "" : value);
        if (safeValue.length() <= maxLength) {
            return safeValue;
        }
        return safeValue.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String sanitize(String value) {
        return new String(
                value.replace('\n', ' ').replace('\r', ' ').getBytes(StandardCharsets.US_ASCII),
                StandardCharsets.US_ASCII
        );
    }

    public record ShippingLabelDocument(String fileName, byte[] content) {
    }
}
