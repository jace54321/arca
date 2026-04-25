# Load .env from project root and start Spring Boot
$envFile = Join-Path (Join-Path $PSScriptRoot "..") ".env"
Get-Content $envFile | ForEach-Object {
    if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
    $parts = $_ -split '=', 2
    if ($parts.Count -eq 2) {
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()
        [System.Environment]::SetEnvironmentVariable($key, $value, "Process")
    }
}

Write-Host "Loaded .env variables" -ForegroundColor Green
& "$PSScriptRoot\mvnw.cmd" spring-boot:run
