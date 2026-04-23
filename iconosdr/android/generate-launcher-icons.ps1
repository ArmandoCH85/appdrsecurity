# Genera capas adaptativas y mipmaps desde playstore.png.
# Recorta márgenes, centra el motivo y escala en modo "contain" para que D·R quepa entero (sin recortes).
$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcPath = Join-Path (Split-Path -Parent $root) "playstore.png"
if (-not (Test-Path $srcPath)) { throw "No existe: $srcPath" }

function Save-Bitmap([System.Drawing.Bitmap]$bmp, [string]$path) {
    $dir = Split-Path -Parent $path
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

function Test-IsBackgroundPixel([System.Drawing.Color]$c) {
    if ($c.A -lt 20) { return $true }
    # Blanco / casi blanco (marco del PNG)
    if ($c.R -gt 248 -and $c.G -gt 248 -and $c.B -gt 248) { return $true }
    return $false
}

function Get-CroppedSource([System.Drawing.Image]$img) {
    $bmp = New-Object System.Drawing.Bitmap($img)
    $W = $bmp.Width
    $H = $bmp.Height
    $minX = $W
    $minY = $H
    $maxX = 0
    $maxY = 0
    $found = $false
    for ($y = 0; $y -lt $H; $y++) {
        for ($x = 0; $x -lt $W; $x++) {
            $c = $bmp.GetPixel($x, $y)
            if (-not (Test-IsBackgroundPixel $c)) {
                $found = $true
                if ($x -lt $minX) { $minX = $x }
                if ($y -lt $minY) { $minY = $y }
                if ($x -gt $maxX) { $maxX = $x }
                if ($y -gt $maxY) { $maxY = $y }
            }
        }
    }
    if (-not $found) {
        $bmp.Dispose()
        return New-Object System.Drawing.Bitmap($img)
    }
    $cw = [Math]::Max(1, $maxX - $minX + 1)
    $ch = [Math]::Max(1, $maxY - $minY + 1)
    $crop = New-Object System.Drawing.Bitmap($cw, $ch)
    $g = [System.Drawing.Graphics]::FromImage($crop)
    $g.DrawImage($bmp, [System.Drawing.Rectangle]::new(0, 0, $cw, $ch), [System.Drawing.Rectangle]::new($minX, $minY, $cw, $ch), [System.Drawing.GraphicsUnit]::Pixel)
    $g.Dispose()
    $bmp.Dispose()
    return $crop
}

function New-ContainedIcon([System.Drawing.Image]$source, [int]$w, [int]$h, [double]$fillFraction, [System.Drawing.Color]$bg) {
    # fillFraction: fracción del lienzo que puede usar el logo (menor = logo más chico, letras completas dentro del círculo).
    $bmp = New-Object System.Drawing.Bitmap($w, $h)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.Clear($bg)

    $sw = [double]$source.Width
    $sh = [double]$source.Height
    $maxW = [double]$w * $fillFraction
    $maxH = [double]$h * $fillFraction
    $ratio = [Math]::Min($maxW / $sw, $maxH / $sh)
    $nw = [int][Math]::Floor($sw * $ratio)
    $nh = [int][Math]::Floor($sh * $ratio)
    $x = [int][Math]::Floor(($w - $nw) / 2)
    $y = [int][Math]::Floor(($h - $nh) / 2)
    $g.DrawImage($source, $x, $y, $nw, $nh)
    $g.Dispose()
    return $bmp
}

$raw = [System.Drawing.Image]::FromFile($srcPath)
$cropped = Get-CroppedSource $raw
$raw.Dispose()

try {
    # Primer plano adaptativo: encajar logo completo (~65% del lienzo = margen para máscara circular en logos anchos)
    $fgFill = 0.65
    $fgSizes = @(
        @{ d = "drawable-mdpi";    px = 108 },
        @{ d = "drawable-hdpi";    px = 162 },
        @{ d = "drawable-xhdpi";   px = 216 },
        @{ d = "drawable-xxhdpi";  px = 324 },
        @{ d = "drawable-xxxhdpi"; px = 432 }
    )
    foreach ($e in $fgSizes) {
        $px = $e.px
        $bmp = New-ContainedIcon $cropped $px $px $fgFill ([System.Drawing.Color]::Transparent)
        Save-Bitmap $bmp (Join-Path (Join-Path $root $e.d) "ic_launcher_foreground.png")
    }

    # Legacy mipmap: un poco más grande que el FG pero sigue contenido
    $legacyFill = 0.74
    $legacy = @(
        @{ d = "mipmap-mdpi";     px = 48 },
        @{ d = "mipmap-hdpi";     px = 72 },
        @{ d = "mipmap-xhdpi";    px = 96 },
        @{ d = "mipmap-xxhdpi";   px = 144 },
        @{ d = "mipmap-xxxhdpi";  px = 192 }
    )
    foreach ($e in $legacy) {
        $px = $e.px
        $bmp = New-ContainedIcon $cropped $px $px $legacyFill ([System.Drawing.Color]::White)
        Save-Bitmap $bmp (Join-Path (Join-Path $root $e.d) "ic_launcher.png")
    }
}
finally {
    $cropped.Dispose()
}

Write-Host "Iconos generados en $root"
