$key = [Environment]::GetEnvironmentVariable("GROQ_API_KEY", "Process")

if ([string]::IsNullOrWhiteSpace($key)) {
    $key = [Environment]::GetEnvironmentVariable("GROQ_API_KEY", "User")
}

if ([string]::IsNullOrWhiteSpace($key)) {
    Write-Error "GROQ_API_KEY is missing."
    exit 1
}

try {
    $response = Invoke-WebRequest `
        -Uri "https://api.groq.com/openai/v1/models" `
        -Headers @{ Authorization = "Bearer $key" } `
        -Method Get `
        -TimeoutSec 30

    Write-Host "Groq key is valid. HTTP $($response.StatusCode)"
    ($response.Content | ConvertFrom-Json).data |
        Select-Object -First 5 -ExpandProperty id
} catch {
    $status = $null
    if ($_.Exception.Response) {
        $status = $_.Exception.Response.StatusCode.value__
    }

    if ($status -eq 401) {
        Write-Error "Groq rejected this key with HTTP 401. Create a new key and run scripts/set-groq-key.ps1 again."
    } elseif ($status) {
        Write-Error "Groq request failed with HTTP $status."
    } else {
        Write-Error "Groq request failed: $($_.Exception.Message)"
    }

    exit 1
}

