
param(
  [string]$Root = ".",
  [string[]]$Keywords = @('TAXKBN'),
  [string[]]$ExcludeDirs = @('.git','node_modules','bin'),
  [string]$Out  = "refs.csv",
  [string]$Sum  = "refs_summary.csv",
  [string]$Stat = "scan_stats.csv",
  [string]$HitDirName = "hit",
  [switch]$Flat
)

# --- helpers -------------------------------------------------------------
try { [System.Text.Encoding]::RegisterProvider([System.Text.CodePagesEncodingProvider]::Instance) } catch {}

$cp932  = [System.Text.Encoding]::GetEncoding(932)
$utf8S  = New-Object System.Text.UTF8Encoding($false, $true)   # strict: throws on invalid bytes
$bomEnc = New-Object System.Text.UTF8Encoding($true)           # UTF-8 with BOM (Excel friendly)

function Get-AbsPath([string]$p) {
  if ([System.IO.Path]::IsPathRooted($p)) { return $p }
  return (Join-Path (Get-Location).Path $p)
}

function Read-Text($bytes) {
  if ($bytes.Length -ge 2 -and $bytes[0] -eq 0xFF -and $bytes[1] -eq 0xFE) {
    return @([System.Text.Encoding]::Unicode.GetString($bytes), 'UTF-16')
  }
  if ($bytes.Length -ge 2 -and $bytes[0] -eq 0xFE -and $bytes[1] -eq 0xFF) {
    return @([System.Text.Encoding]::BigEndianUnicode.GetString($bytes), 'UTF-16')
  }
  try   { return @($utf8S.GetString($bytes), 'UTF-8') }
  catch { return @($cp932.GetString($bytes),  'CP932') }
}

# --- settings ------------------------------------------------------------
$ext = '*.sql','*.pls','*.pks','*.pkb','*.java','*.jsp','*.xml','*.html','*.js','*.properties','*.fex','*.mas'

$opt = [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
$kwRegex = @{}
foreach ($k in $Keywords) {
  $kwRegex[$k] = New-Object System.Text.RegularExpressions.Regex([regex]::Escape($k), $opt)
}
$pattern = (($Keywords | ForEach-Object { [regex]::Escape($_) }) -join '|')
$all = New-Object System.Text.RegularExpressions.Regex($pattern, $opt)

$Out  = Get-AbsPath $Out
$Sum  = Get-AbsPath $Sum
$Stat = Get-AbsPath $Stat

# hit folder = under the folder where this ps1 file is located
$scriptDir = if ($PSScriptRoot) { $PSScriptRoot } else { (Get-Location).Path }
$hitDir    = Join-Path $scriptDir $HitDirName
$hitPrefix = $hitDir.TrimEnd('\') + '\'

# --- collect files (exclude folders by relative path under Root) ----------
$rootFull  = (Resolve-Path -LiteralPath $Root).Path.TrimEnd('\')
$exPattern = '\\(' + (($ExcludeDirs | ForEach-Object { [regex]::Escape($_) }) -join '|') + ')\\'

Write-Host "Root     : $rootFull"
Write-Host "Keywords : $($Keywords -join ', ')"
Write-Host "Excluded : $($ExcludeDirs -join ', ')"
Write-Host "Hit dir  : $hitDir"

# the hit folder itself is never scanned (otherwise copies would be hit again)
$files = @(Get-ChildItem -LiteralPath $rootFull -Recurse -File -Include $ext |
           Where-Object {
             ($_.FullName.Substring($rootFull.Length) -notmatch $exPattern) -and
             (-not $_.FullName.StartsWith($hitPrefix, [System.StringComparison]::OrdinalIgnoreCase))
           })

Write-Host "Files to scan: $($files.Count)"

# --- scan ----------------------------------------------------------------
$rows     = New-Object System.Collections.Generic.List[object]
$fileKw   = @{}
$fileLine = @{}
$fileEnc  = @{}
$stats    = @{}

foreach ($f in $files) {
  $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
  $r = Read-Text $bytes
  $text = $r[0]; $enc = $r[1]

  $e = $f.Extension.ToLower()
  if (-not $stats.ContainsKey($e)) {
    $stats[$e] = [pscustomobject]@{ Ext=$e; Files=0; FilesWithHit=0; UTF8=0; CP932=0; UTF16=0 }
  }
  $stats[$e].Files++
  if     ($enc -eq 'UTF-8') { $stats[$e].UTF8++ }
  elseif ($enc -eq 'CP932') { $stats[$e].CP932++ }
  else                      { $stats[$e].UTF16++ }

  $n = 0; $hit = $false
  foreach ($line in ($text -split "\r?\n")) {
    $n++
    if ($all.IsMatch($line)) {
      $hit = $true
      $p = $f.FullName
      if (-not $fileKw.ContainsKey($p)) { $fileKw[$p] = @{}; $fileLine[$p] = 0; $fileEnc[$p] = $enc }
      $fileLine[$p]++
      $hitKw = @()
      foreach ($k in $Keywords) {
        if ($kwRegex[$k].IsMatch($line)) {
          $hitKw += $k
          $fileKw[$p][$k] = 1 + [int]$fileKw[$p][$k]
        }
      }
      $rows.Add([pscustomobject]@{
        Path       = $p
        LineNumber = $n
        Encoding   = $enc
        Keywords   = ($hitKw -join ',')
        Line       = $line.Trim()
      })
    }
  }
  if ($hit) { $stats[$e].FilesWithHit++ }
}

# --- summary per file ----------------------------------------------------
$summary = @( @(foreach ($p in $fileKw.Keys) {
  $h = $fileKw[$p]
  [pscustomobject]@{
    Lines    = $fileLine[$p]
    Keywords = (($Keywords | Where-Object { $h.ContainsKey($_) } | ForEach-Object { "$_($($h[$_]))" }) -join ' ')
    Encoding = $fileEnc[$p]
    Path     = $p
  }
}) | Sort-Object Lines -Descending )

$statOut = @($stats.Values | Sort-Object Files -Descending)

# --- write csv (UTF-8 with BOM) -------------------------------------------
[System.IO.File]::WriteAllLines($Out,  @($rows    | ConvertTo-Csv -NoTypeInformation), $bomEnc)
[System.IO.File]::WriteAllLines($Sum,  @($summary | ConvertTo-Csv -NoTypeInformation), $bomEnc)
[System.IO.File]::WriteAllLines($Stat, @($statOut | ConvertTo-Csv -NoTypeInformation), $bomEnc)

# --- copy hit files into the hit folder -----------------------------------
$copied = 0
$failed = New-Object System.Collections.Generic.List[string]

if ($summary.Count -gt 0) {
  if (-not (Test-Path -LiteralPath $hitDir)) {
    New-Item -ItemType Directory -Path $hitDir | Out-Null
  }
  foreach ($s in $summary) {
    $src = $s.Path
    $rel = $src.Substring($rootFull.Length).TrimStart('\')
    try {
      if ($Flat) {
        $dst = Join-Path $hitDir ($rel -replace '[\\/:]', '__')
      } else {
        $dst    = Join-Path $hitDir $rel
        $parent = Split-Path -Path $dst -Parent
        if (-not (Test-Path -LiteralPath $parent)) {
          New-Item -ItemType Directory -Path $parent -Force | Out-Null
        }
      }
      Copy-Item -LiteralPath $src -Destination $dst -Force -ErrorAction Stop
      $copied++
    } catch {
      $failed.Add("$src : $($_.Exception.Message)")
    }
  }
  if ($failed.Count -gt 0) {
    $errFile = Join-Path (Split-Path -Path $Stat -Parent) "copy_errors.txt"
    [System.IO.File]::WriteAllLines($errFile, $failed.ToArray(), $bomEnc)
    Write-Host "Copy failed: $($failed.Count) -> $errFile"
  }
}

# --- report ---------------------------------------------------------------
$statOut | Format-Table -AutoSize
Write-Host "Hit lines : $($rows.Count)"
Write-Host "Hit files : $(@($summary).Count)"
Write-Host "Copied    : $copied -> $hitDir"
Write-Host "Output    : $Out"
Write-Host "            $Sum"
Write-Host "            $Stat"


