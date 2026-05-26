param(
    [switch] $ProcessOnly
)

$secureKey = Read-Host "Paste your Groq API key" -AsSecureString
$plainKey = [System.Net.NetworkCredential]::new("", $secureKey).Password.Trim()

if ([string]::IsNullOrWhiteSpace($plainKey)) {
    Write-Error "No key provided."
    exit 1
}

if (-not $plainKey.StartsWith("gsk_")) {
    Write-Warning "Groq keys normally start with 'gsk_'. Check that you pasted the right key."
}

$env:GROQ_API_KEY = $plainKey

if (-not $ProcessOnly) {
    [Environment]::SetEnvironmentVariable("GROQ_API_KEY", $plainKey, "User")
    Write-Host "GROQ_API_KEY saved to the current process and user environment."
    Write-Host "Open a new terminal before running the app, or keep using this terminal."
} else {
    Write-Host "GROQ_API_KEY set for this PowerShell process only."
}

