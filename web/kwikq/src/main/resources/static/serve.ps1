$prefix = "http://localhost:8000/"
$root = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
Set-Location $root
$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add($prefix)
$listener.Start()
Write-Output "Serving $root on $prefix. Press Ctrl+C to stop."
while ($listener.IsListening) {
    try {
        $context = $listener.GetContext()
    } catch {
        break
    }
    $request = $context.Request
    $path = $request.Url.AbsolutePath.TrimStart('/')
    if ([string]::IsNullOrEmpty($path)) { $path = "index.html" }
    $file = Join-Path $root $path
    if (Test-Path $file) {
        $bytes = [System.IO.File]::ReadAllBytes($file)
        $ext = [System.IO.Path]::GetExtension($file).ToLower()
        switch ($ext) {
            ".html" { $type = "text/html" }
            ".css"  { $type = "text/css" }
            ".js"   { $type = "application/javascript" }
            ".png"  { $type = "image/png" }
            ".jpg"  { $type = "image/jpeg" }
            ".jpeg" { $type = "image/jpeg" }
            ".svg"  { $type = "image/svg+xml" }
            ".json" { $type = "application/json" }
            default  { $type = "application/octet-stream" }
        }
        $context.Response.ContentType = $type
        $context.Response.ContentLength64 = $bytes.Length
        $context.Response.OutputStream.Write($bytes, 0, $bytes.Length)
    } else {
        $context.Response.StatusCode = 404
        $msg = "Not Found"
        $b = [System.Text.Encoding]::UTF8.GetBytes($msg)
        $context.Response.ContentLength64 = $b.Length
        $context.Response.OutputStream.Write($b, 0, $b.Length)
    }
    $context.Response.OutputStream.Close()
}
$listener.Stop()
